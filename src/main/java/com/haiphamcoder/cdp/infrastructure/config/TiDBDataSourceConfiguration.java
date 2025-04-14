package com.haiphamcoder.cdp.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
    @Bean(name = "tidbJpaProperties")
    @ConfigurationProperties(prefix = "jpa.tidb")
    JpaProperties tidbJpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "tidbDataSource")
    @ConfigurationProperties(prefix = "datasource.tidb")
    DataSource tidbDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "tidbEntityManager")
    EntityManager tidbEntityManager(
            @Qualifier("tidbEntityManagerFactory") LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory) {
        return tidbEntityManagerFactory.getObject().createEntityManager();
    }

    @Bean(name = "tidbEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("tidbJpaProperties") JpaProperties tidbJpaProperties,
            @Qualifier("tidbDataSource") DataSource tidbDataSource) {
        return DataSourceUtils.createEntityManagerFactoryBean(
                entityManagerFactoryBuilder,
                tidbDataSource,
                tidbJpaProperties,
                "tidb",
                "com.haiphamcoder.cdp.domain.entity.raw");
    }

    @Bean(name = "tidbTransactionManager")
    PlatformTransactionManager tidbTransactionManager(
            @Qualifier("tidbEntityManagerFactory") LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory) {
        return DataSourceUtils.createTransactionManager(tidbEntityManagerFactory.getObject());
    }
}
