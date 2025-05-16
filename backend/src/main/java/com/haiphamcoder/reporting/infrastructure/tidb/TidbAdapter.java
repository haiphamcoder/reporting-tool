package com.haiphamcoder.reporting.infrastructure.tidb;

import java.io.Closeable;
import java.sql.Connection;

public interface TidbAdapter extends Closeable {

    Connection getConnection();

}
