package com.fsc.metric.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于从外部cfg目录中获取对应的配置 <b>server.type</b>  等信息
 *
 * @author tc
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvPostProcessor implements EnvironmentPostProcessor {

    private final PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();

    // 配置加载相关
    public static String CFG_BASE_PATH = "./cfg/";
    private final String SUFFIX = ".properties";

    // 配置重写相关
    private final String LOAD_NEO4J_ENGINE = "graph.metric.neo4j.on";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        File file = new File(CFG_BASE_PATH);
        if (file.exists() && file.isDirectory()) {
            Map<String, Object> customPropertyMap = new ConcurrentHashMap<>(4);
            for (File cfgFile : Objects.requireNonNull(file.listFiles())) {
                if (cfgFile.getName().endsWith(SUFFIX)) {
                    loadPropertiesAndAddToEnv(cfgFile, environment, customPropertyMap);
                }
            }
            // 添加自定义配置
            if (customPropertyMap.size() > 0) {
                environment.getPropertySources().addFirst(new MapPropertySource("custom-cfg", customPropertyMap));
            }
        }
    }

    private PropertySource<?> loadProperties(Resource path, String resourceName) {
        if (!path.exists()) {
            return null;
        }
        try {
            // custom resource
            List<PropertySource<?>> propertySourceList = this.loader.load("cr-" + resourceName, path);
            if (propertySourceList.size() > 0) {
                return this.loader.load("cr-" + resourceName, path).get(0);
            }
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    private boolean loadPropertiesAndAddToEnv(File cfgFile, ConfigurableEnvironment environment, Map<String, Object> customPropertyMap) {
//        Resource path = new ClassPathResource(CFG_BASE_PATH + cfgName + SUFFIX);
//        PropertySource<?> propertySource =
        FileSystemResource path = new FileSystemResource(cfgFile);
        PropertySource<?> propertySource = loadProperties(path, cfgFile.getName());
        if (propertySource == null) {
            return false;
        }
        // 这里如果正常解析到了，就添加进去
        environment.getPropertySources().addLast(propertySource);
        // 尝试添加额外的配置（比如server.port）
        tryUpdateCustomProperties(propertySource, customPropertyMap);
        return true;
    }

    private void tryUpdateCustomProperties(PropertySource<?> smCfgPropertiesSource, Map<String, Object> customPropertyMap) {
        // 如果本地环境中有对应的配置，如rewrite.server.port=1, 则重写
        Object loadNeo4jEngine = smCfgPropertiesSource.getProperty(LOAD_NEO4J_ENGINE);
        if (!"true".equals(loadNeo4jEngine)) {
            customPropertyMap.put(LOAD_NEO4J_ENGINE, "false");
        } else {
            customPropertyMap.put(LOAD_NEO4J_ENGINE, "true");
        }
    }
}
