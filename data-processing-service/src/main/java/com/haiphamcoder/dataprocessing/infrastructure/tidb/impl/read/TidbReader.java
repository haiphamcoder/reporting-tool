package com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.read;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

}
