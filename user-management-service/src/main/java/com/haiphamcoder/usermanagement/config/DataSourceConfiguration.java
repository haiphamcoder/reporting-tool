package com.haiphamcoder.usermanagement.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.haiphamcoder.usermanagement.shared.DataSourceUtils;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.haiphamcoder.usermanagement.repository.impl" }, entityManagerFactoryRef = "mainEntityManagerFactory", transactionManagerRef = "mainTransactionManager")
public class DataSourceConfiguration {

    @Bean(name = "mainDataSourceProperties")
    @ConfigurationProperties(prefix = "datasource")
    @Primary
    DataSourceProperties mainDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mainDataSource")
    @Primary
    DataSource mainDataSource(@Qualifier("mainDataSourceProperties") DataSourceProperties mainDataSourceProperties,
            @Value("${datasource.pool.size}") int poolSize,
            @Value("${datasource.pool.name}") String poolName) {
        HikariDataSource dataSource = mainDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setMaximumPoolSize(poolSize);
        dataSource.setPoolName(poolName);
        return dataSource;
    }

    @Bean(name = "mainEntityManagerFactory")
    @Primary
    LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("mainDataSource") DataSource mainDataSource) {
        return DataSourceUtils.createEntityManagerFactoryBean(
                entityManagerFactoryBuilder,
                mainDataSource,
                "main",
                "com.haiphamcoder.usermanagement.domain.entity");
    }

    @Bean(name = "mainTransactionManager")
    @Primary
    PlatformTransactionManager mainTransactionManager(
            @Qualifier("mainEntityManagerFactory") LocalContainerEntityManagerFactoryBean mainEntityManagerFactory) {
        return DataSourceUtils.createTransactionManager(mainEntityManagerFactory.getObject());
    }
}
