package com.fsc.metric.config;

import com.fsc.common.config.ConfigTools3;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class WebServerConfig {

    private Logger logger = LoggerFactory.getLogger(WebServerConfig.class);

    @Bean
    public WebServerFactoryCustomizer<WebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            if (factory instanceof UndertowServletWebServerFactory) {
                UndertowServletWebServerFactory undertow = (UndertowServletWebServerFactory) factory;
                undertow.setPort(getPort());
                undertow.addBuilderCustomizers(builder -> {
                    builder.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, getServerTimeout())
                            .setSocketOption(UndertowOptions.IDLE_TIMEOUT, 5000)
                            .setIoThreads(getIoThreads())
                            .setWorkerThreads(getWorkerThreads())
                            .setByteBufferPool(new DefaultByteBufferPool(true, getPoolBufSize()));
                });
                logger.info("start port {}, buf-size:{} ", getPort(), getPoolBufSize());
            }
        };
    }


    public int getPort() {
        return ConfigTools3.getConfigAsInt("graph.metric.server.port", 2089);
    }

    public int getServerTimeout() {
        return ConfigTools3.getConfigAsInt("graph.metric.server.connection.timeout", 30000);
    }

    public int getIoThreads() {
        return ConfigTools3.getConfigAsInt("graph.metric.server.io.threads", 32);
    }

    public int getWorkerThreads() {
        return ConfigTools3.getConfigAsInt("graph.metric.server.worker.threads", 1024);
    }

    public int getPoolBufSize() {
        return ConfigTools3.getConfigAsInt("graph.metric.server.pool.buffer.size", 16 * 1024);
    }

}
