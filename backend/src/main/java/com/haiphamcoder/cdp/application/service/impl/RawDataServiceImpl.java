package com.haiphamcoder.cdp.application.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.RawDataService;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RawDataServiceImpl implements RawDataService {
    private final EntityManager entityManager;
    private final String tableNamePrefix;

    public RawDataServiceImpl(@Qualifier("tidbEntityManager") EntityManager entityManager,
            @Value("${tidb.raw-data.table-name-prefix:raw_data_}") String tableNamePrefix) {
        this.entityManager = entityManager;
        this.tableNamePrefix = tableNamePrefix;
    }

    @Override
    public void createTable(Long userId, Long sourceId, Map<String, String> columnMap) {
        String tableName = tableNamePrefix + "_" + userId + "_" + sourceId;
        String sql = buildCreateTableSql(tableName, columnMap);
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    private String buildCreateTableSql(String tableName, Map<String, String> columnMap) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS " + tableName + " ( _id BIGINT AUTO_INCREMENT PRIMARY KEY");
        for (Map.Entry<String, String> entry : columnMap.entrySet()) {
            sql.append(", " + entry.getKey() + " " + entry.getValue());
        }
        sql.append(")");
        return sql.toString();
    }
}
