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

import jakarta.persistence.EntityManager;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.haiphamcoder.cdp.adapter.persistence" }, entityManagerFactoryRef = "tidbEntityManagerFactory", transactionManagerRef = "tidbTransactionManager")
public class TiDBDataSourceConfiguration {
    @Bean(name = "tidbJpaProperties")
    @ConfigurationProperties(prefix = "jpa.tidb")
    @Primary
    JpaProperties tidbJpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "tidbDataSource")
    @ConfigurationProperties(prefix = "datasource.primary")
    @Primary
    DataSource tidbDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "tidbEntityManager")
    EntityManager tidbEntityManager(
            @Qualifier("tidbEntityManagerFactory") LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory) {
        return tidbEntityManagerFactory.getObject().createEntityManager();
    }

    @Bean(name = "tidbEntityManagerFactory")
    @Primary
    LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("tidbJpaProperties") JpaProperties mainJpaProperties,
            @Qualifier("tidbDataSource") DataSource mainDataSource) {
        return DataSourceUtils.createEntityManagerFactoryBean(
                entityManagerFactoryBuilder,
                mainDataSource,
                mainJpaProperties,
                "tidb",
                "com.haiphamcoder.cdp.domain.entity");
    }

    @Bean(name = "tidbTransactionManager")
    @Primary
    PlatformTransactionManager tidbTransactionManager(
            @Qualifier("tidbEntityManagerFactory") LocalContainerEntityManagerFactoryBean mainEntityManagerFactory) {
        return DataSourceUtils.createTransactionManager(mainEntityManagerFactory.getObject());
    }
}
