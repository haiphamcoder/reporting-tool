package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Map;

public interface TiDBRepository {

    boolean createTable(String tableName, Map<String, String> tableSchema);
    
    String createTable(String tableName, String tableSchema);

    String deleteTable(String tableName);

    boolean insertData(String tableName, List<String> schema, List<String> values);

}