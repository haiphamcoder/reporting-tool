package com.haiphamcoder.cdp.domain.repository;

import java.util.List;

public interface TiDBRepository {
    
    String createTable(String tableName, String tableSchema);

    String deleteTable(String tableName);

    boolean insertData(String tableName, List<String> schema, List<String> values);

}