package com.haiphamcoder.cdp.domain.repository;

public interface TiDBRepository {
    
    String createTable(String tableName, String tableSchema);

    String deleteTable(String tableName);

}