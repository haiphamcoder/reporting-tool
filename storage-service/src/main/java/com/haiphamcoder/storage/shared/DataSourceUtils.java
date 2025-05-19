package com.haiphamcoder.storage.shared;

import javax.sql.DataSource;

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
            String persistenceUnitName,
            String entityPackage) {
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages(entityPackage)
                .persistenceUnit(persistenceUnitName)
                .build();
    }

    public static PlatformTransactionManager createTransactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
