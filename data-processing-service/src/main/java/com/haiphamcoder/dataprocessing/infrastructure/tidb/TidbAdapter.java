package com.haiphamcoder.dataprocessing.infrastructure.tidb;

import java.io.Closeable;
import java.sql.Connection;

public interface TidbAdapter extends Closeable {

    Connection getConnection();

}
