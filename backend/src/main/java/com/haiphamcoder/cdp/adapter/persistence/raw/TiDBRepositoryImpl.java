package com.haiphamcoder.cdp.adapter.persistence.raw;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.domain.repository.TiDBRepository;

import jakarta.persistence.EntityManager;

@Component
public class TiDBRepositoryImpl implements TiDBRepository{

    private final EntityManager entityManager;

    public TiDBRepositoryImpl(@Qualifier("tidbEntityManager") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public String createTable(String tableName, String tableSchema) {
        entityManager.createNativeQuery("CREATE TABLE " + tableName + " (" + tableSchema + ")").executeUpdate();
        return "Table created successfully";
    }

    @Override
    public String deleteTable(String tableName) {
        entityManager.createNativeQuery("DROP TABLE " + tableName).executeUpdate();
        return "Table deleted successfully";
    }

    @Override
    public boolean insertData(String tableName, List<String> schema, List<String> values) {
        entityManager.createNativeQuery("INSERT INTO " + tableName + " (" + String.join(", ", schema) + ") VALUES (" + String.join(", ", values) + ")").executeUpdate();
        return true;
    }
    
}
