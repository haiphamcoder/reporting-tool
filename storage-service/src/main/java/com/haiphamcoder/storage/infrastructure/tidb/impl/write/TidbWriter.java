package com.haiphamcoder.storage.infrastructure.tidb.impl.write;

import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.haiphamcoder.storage.infrastructure.tidb.impl.TidbAdapterImpl;

@Slf4j
public class TidbWriter extends TidbAdapterImpl {

    public TidbWriter(String url, String username, String password) {
        super(url, username, password);
    }

    public void insert(String table, Map<String, Object> data) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table).append(" (");
        StringBuilder values = new StringBuilder("VALUES (");

        data.keySet().forEach(key -> {
            sql.append(key).append(",");
            values.append(data.get(key)).append(",");
        });

        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");
        sql.append(values);

        executeUpdate(sql.toString());
    }

    public void batchInsert(String table, List<String> columns, List<JSONObject> dataList)
            throws SQLException {
        if (dataList.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table).append(" (");
        columns.forEach(column -> sql.append(column).append(","));
        sql.deleteCharAt(sql.length() - 1).append(") ");

        StringBuilder values = new StringBuilder(" VALUES ");
        for (JSONObject data : dataList) {
            values.append("(");
            columns.forEach(column -> {
                if (data.get(column) instanceof String) {
                    values.append("'").append(data.get(column)).append("',");
                } else {
                    values.append(data.get(column)).append(",");
                }
            });
            values.deleteCharAt(values.length() - 1).append("),");
        }
        values.deleteCharAt(values.length() - 1);
        sql.append(values);
        executeUpdate(sql.toString());
    }

    private void doExecuteUpdate(Statement statement, String sql) throws SQLException {
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            log.error("Execute update failed! {}", e.getMessage());
            throw e;
        }
    }

    public void executeUpdate(String sql) throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
            doExecuteUpdate(statement, sql);
        } catch (SQLException e) {
            log.error("Execute update failed! {}", e.getMessage());
            throw e;
        }
    }

    public void createTable(String table, Map<String, String> schema) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");
        schema.forEach((key, value) -> sql.append(key).append(" ").append(value).append(","));
        sql.deleteCharAt(sql.length() - 1).append(")");
        executeUpdate(sql.toString());
    }

}
