package com.haiphamcoder.cdp.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.haiphamcoder.cdp.shared.DataSourceUtils;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManager;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.haiphamcoder.cdp.adapter.persistence.raw" }, entityManagerFactoryRef = "tidbEntityManagerFactory", transactionManagerRef = "tidbTransactionManager")
public class TiDBDataSourceConfiguration {

    @Bean(name = "tidbDataSourceProperties")
    @ConfigurationProperties(prefix = "datasource.secondary")
    DataSourceProperties tidbDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "tidbDataSource")
    DataSource tidbDataSource(@Qualifier("tidbDataSourceProperties") DataSourceProperties tidbDataSourceProperties,
            @Value("${datasource.secondary.pool.size}") int poolSize,
            @Value("${datasource.secondary.pool.name}") String poolName) {
        HikariDataSource dataSource = tidbDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setMaximumPoolSize(poolSize);
        dataSource.setPoolName(poolName);
        return dataSource;
    }

    @Bean(name = "tidbEntityManager")
    EntityManager tidbEntityManager(
            @Qualifier("tidbEntityManagerFactory") LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory) {
        return tidbEntityManagerFactory.getObject().createEntityManager();
    }

    @Bean(name = "tidbEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("tidbDataSource") DataSource tidbDataSource) {
        return DataSourceUtils.createEntityManagerFactoryBean(
                entityManagerFactoryBuilder,
                tidbDataSource,
                "tidb",
                "com.haiphamcoder.cdp.domain.entity.raw");
    }

    @Bean(name = "tidbTransactionManager")
    PlatformTransactionManager tidbTransactionManager(
            @Qualifier("tidbEntityManagerFactory") LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory) {
        return DataSourceUtils.createTransactionManager(tidbEntityManagerFactory.getObject());
    }
}
