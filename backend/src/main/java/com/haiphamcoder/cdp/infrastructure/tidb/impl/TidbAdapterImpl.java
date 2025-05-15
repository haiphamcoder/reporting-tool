package com.haiphamcoder.cdp.infrastructure.tidb.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.haiphamcoder.cdp.infrastructure.tidb.TidbAdapter;

public class TidbAdapterImpl implements TidbAdapter {

    private Connection connection;

    public TidbAdapterImpl(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to TiDB", e);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException("Failed to close connection", e);
        }
    }

}
