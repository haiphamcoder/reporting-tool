package com.haiphamcoder.cdp.shared;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataSourceUtils {
    public static LocalContainerEntityManagerFactoryBean createEntityManagerFactoryBean(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            DataSource dataSource,
            JpaProperties jpaProperties,
            String persistenceUnitName,
            String entityPackage) {
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages(entityPackage)
                .persistenceUnit(persistenceUnitName)
                .properties(jpaProperties.getProperties())
                .build();
    }

    public static PlatformTransactionManager createTransactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
