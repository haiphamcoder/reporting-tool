package com.haiphamcoder.cdp.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.haiphamcoder.cdp.shared.DataSourceUtils;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.haiphamcoder.cdp.adapter.persistence" }, entityManagerFactoryRef = "mainEntityManagerFactory", transactionManagerRef = "mainTransactionManager")
public class MainDataSourceConfiguration {
    @Bean(name = "mainJpaProperties")
    @ConfigurationProperties(prefix = "jpa.primary")
    @Primary
    JpaProperties mainJpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "mainDataSource")
    @ConfigurationProperties(prefix = "datasource.primary")
    @Primary
    DataSource mainDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "mainEntityManagerFactory")
    @Primary
    LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("mainJpaProperties") JpaProperties mainJpaProperties,
            @Qualifier("mainDataSource") DataSource mainDataSource) {
        return DataSourceUtils.createEntityManagerFactoryBean(
                entityManagerFactoryBuilder,
                mainDataSource,
                mainJpaProperties,
                "main",
                "com.haiphamcoder.cdp.domain.entity");
    }

    @Bean(name = "mainTransactionManager")
    @Primary
    PlatformTransactionManager mainTransactionManager(
            @Qualifier("mainEntityManagerFactory") LocalContainerEntityManagerFactoryBean mainEntityManagerFactory) {
        return DataSourceUtils.createTransactionManager(mainEntityManagerFactory.getObject());
    }
}
