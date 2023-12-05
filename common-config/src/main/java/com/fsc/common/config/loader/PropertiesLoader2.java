package com.fsc.common.config.loader;


import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader2 {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader2.class);
    private static final String FILE_CODING = "utf-8";

    public PropertiesLoader2() {
    }

    public static Map<String, String> loader(File file) throws IOException {
        logger.info("Load File:[{}]", file.getAbsoluteFile());
        Map<String, String> resultMap = (Map)Files.readLines(file, Charset.forName("utf-8"), new LineProcessor<Map<String, String>>() {
            Map<String, String> resultMap = Maps.newLinkedHashMap();

            public boolean processLine(String line) throws IOException {
                if (!Strings.isNullOrEmpty(line) && !line.startsWith("#")) {
                    int pos = line.indexOf("=");
                    if (pos != -1) {
                        String key = line.substring(0, pos);
                        String value = line.substring(pos + 1);
                        this.resultMap.put(key, value);
                        PropertiesLoader2.logger.info("Load properties: [{}={}]", key, value);
                    }
                }

                return true;
            }

            public Map<String, String> getResult() {
                return this.resultMap;
            }
        });
        return resultMap;
    }
}
