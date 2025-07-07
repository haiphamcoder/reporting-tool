package com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.read;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.TidbAdapterImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TidbReader extends TidbAdapterImpl {

    public TidbReader(String url, String username, String password) {
        super(url, username, password);
    }

    private CustomResultSet doExecuteQuery(Statement statement, String sql) throws SQLException {
        try {
            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery(sql);
            long endTime = System.currentTimeMillis();
            log.info("Query executed in {} ms", endTime - startTime);
            return new CustomResultSet(resultSet, endTime - startTime);
        } catch (SQLException e) {
            log.error("Query execution failed! {}", e.getMessage());
            throw e;
        }
    }

    public CustomResultSet executeQuery(String sql) throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
            return doExecuteQuery(statement, sql);
        } catch (SQLException e) {
            log.error("Query execution failed! {}", e.getMessage());
            throw e;
        }
    }

    public List<JSONObject> getPreviewData(String tableName, List<Mapping> mappings, String search, String searchBy,
            Integer page, Integer limit)
            throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        mappings.forEach(mapping -> {
            if (mapping.getIsHidden() == false) {
                sql.append(mapping.getFieldMapping() + ",");
            }
        });
        sql.append("_id_,");
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" FROM ");
        sql.append(tableName);
        if (search != null && searchBy != null) {
            for (Mapping mapping : mappings) {
                if (mapping.getFieldMapping().equals(searchBy)) {
                    sql.append(" WHERE ");
                    if (mapping.getFieldType().equalsIgnoreCase("text")) {
                        sql.append(mapping.getFieldMapping());
                        sql.append(" COLLATE utf8mb4_general_ci");
                        sql.append(" LIKE ");
                        sql.append("'%");
                        sql.append(search);
                        sql.append("%'");
                    } else {
                        sql.append(mapping.getFieldMapping());
                        sql.append(" = ");
                        sql.append("'");
                        sql.append(search);
                        sql.append("'");
                    }
                    break;
                }
            }
        }
        sql.append(" LIMIT ");
        sql.append(limit);
        sql.append(" OFFSET ");
        sql.append(page * limit);

        CustomResultSet resultSet = executeQuery(sql.toString());
        List<Map<String, Object>> data = resultSet.getRows();
        List<JSONObject> records = new LinkedList<>();
        for (Map<String, Object> row : data) {
            JSONObject record = new JSONObject();
            for (Mapping mapping : mappings) {
                if (mapping.getIsHidden() == false) {
                    record.put(mapping.getFieldMapping(), row.get(mapping.getFieldMapping()));
                }
            }
            record.put("_id_", row.get("_id_"));
            records.add(record);
        }
        return records;
    }

    public List<JSONObject> getPreviewDataByQuery(String sqlQuery) throws SQLException {
        CustomResultSet resultSet = executeQuery(sqlQuery);
        List<Map<String, Object>> data = resultSet.getRows();

        List<JSONObject> records = new LinkedList<>();
        for (Map<String, Object> row : data) {
            log.info("Row: {}", row);
            JSONObject record = new JSONObject();
            for (Entry<String, Object> entry : row.entrySet()) {
                record.put(entry.getKey(), entry.getValue());
            }
            records.add(record);
        }
        return records;
    }

}
