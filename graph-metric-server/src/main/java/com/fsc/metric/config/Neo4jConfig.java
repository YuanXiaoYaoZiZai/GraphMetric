package com.fsc.metric.config;

import com.fsc.common.config.ConfigTools3;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@EnableNeo4jRepositories(
        basePackages = "com.fsc.metric.dao",
        sessionFactoryRef = "neo4jSessionFactory",
        transactionManagerRef = "neo4jTransactionManager")
@EntityScan(basePackages = "com.fsc.metric.dao")
@EnableTransactionManagement
@ConditionalOnExpression("'${graph.metric.neo4j.on}'.equals('true')")
public class Neo4jConfig {

    private static final Logger LOG = LoggerFactory.getLogger(Neo4jConfig.class);

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        String uri = ConfigTools3.getString("graph.metric.neo4j.uri","bolt://149.56.24.80:7687");
        String userName = ConfigTools3.getString("graph.metric.neo4j.username","neo4j");
        String password = ConfigTools3.getString("graph.metric.neo4j.password","B7Nw8zIiAFlI2Gv");

        LOG.info("Connect neo4j:{}:{}:{}", uri, userName, password);
        return new org.neo4j.ogm.config.Configuration.Builder().uri(uri).connectionPoolSize(100)
                .credentials(userName, password).build();
    }

    @Bean("neo4jSessionFactory")
    public SessionFactory sessionFactory(org.neo4j.ogm.config.Configuration configuration,
            ApplicationContext applicationContext,
            ObjectProvider<EventListener> eventListeners) {
        SessionFactory sessionFactory = new SessionFactory(configuration,
                getPackagesToScan(applicationContext));
        eventListeners.stream().forEach(sessionFactory::register);
        return sessionFactory;
    }

    @Bean("neo4jTransactionManager")
    public Neo4jTransactionManager neo4jTransactionManager(SessionFactory sessionFactory,
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        return customize(new Neo4jTransactionManager(sessionFactory),
                transactionManagerCustomizers.getIfAvailable());
    }

    private String[] getPackagesToScan(ApplicationContext applicationContext) {
        List<String> packages = EntityScanPackages.get(applicationContext)
                .getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(applicationContext)) {
            packages = AutoConfigurationPackages.get(applicationContext);
        }
        return StringUtils.toStringArray(packages);
    }

    private Neo4jTransactionManager customize(Neo4jTransactionManager transactionManager,
            TransactionManagerCustomizers customizers) {
        if (customizers != null) {
            customizers.customize(transactionManager);
        }
        return transactionManager;
    }
}
