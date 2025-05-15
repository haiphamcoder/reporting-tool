package com.haiphamcoder.cdp.infrastructure.tidb.impl.write;

import com.haiphamcoder.cdp.infrastructure.tidb.impl.TidbAdapterImpl;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TidbWriter extends TidbAdapterImpl {

    public TidbWriter(String url, String username, String password) {
        super(url, username, password);
    }

    public void insert(String table, Map<String, Object> data) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table).append(" (");
        StringBuilder values = new StringBuilder("VALUES (");

        data.keySet().forEach(key -> {
            sql.append(key).append(",");
            values.append("?,");
        });

        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");
        sql.append(values);

        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            stmt.executeUpdate();
        }
    }

    public void batchInsert(String table, List<String> columns, List<Map<String, Object>> dataList)
            throws SQLException {
        if (dataList.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table).append(" (");
        columns.forEach(column -> sql.append(column).append(","));
        sql.deleteCharAt(sql.length() - 1).append(") ");

        StringBuilder values = new StringBuilder("VALUES (");
        columns.forEach(column -> values.append("?,"));
        values.deleteCharAt(values.length() - 1).append(")");

        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            for (Map<String, Object> data : dataList) {
                int index = 1;
                for (String column : columns) {
                    stmt.setObject(index++, data.get(column));
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void update(String table, Map<String, Object> data, String whereClause, Object... params)
            throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");

        data.keySet().forEach(key -> sql.append(key).append("=?,"));
        sql.deleteCharAt(sql.length() - 1);

        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            for (Object param : params) {
                stmt.setObject(index++, param);
            }
            stmt.executeUpdate();
        }
    }

    public void delete(String table, String whereClause, Object... params) throws SQLException {
        String sql = "DELETE FROM " + table;
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        }
    }

    public void batchInsert(String table, List<Map<String, Object>> dataList) throws SQLException {
        if (dataList.isEmpty())
            return;

        Map<String, Object> firstRow = dataList.get(0);
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table).append(" (");
        StringBuilder values = new StringBuilder("VALUES (");

        firstRow.keySet().forEach(key -> {
            sql.append(key).append(",");
            values.append("?,");
        });

        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");
        sql.append(values);

        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            for (Map<String, Object> data : dataList) {
                int index = 1;
                for (String key : firstRow.keySet()) {
                    stmt.setObject(index++, data.get(key));
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        }
    }

    public void createTable(String table, Map<String, String> schema) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");
        schema.forEach((key, value) -> sql.append(key).append(" ").append(value).append(","));
        sql.deleteCharAt(sql.length() - 1).append(")");
        executeUpdate(sql.toString());
    }

}
