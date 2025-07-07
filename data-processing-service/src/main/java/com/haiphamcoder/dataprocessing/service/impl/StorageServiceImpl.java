package com.haiphamcoder.dataprocessing.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.read.TidbReader;
import com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.read.CustomResultSet;
import com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.write.TidbWriter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final String url;
    private final String username;
    private final String password;

    public StorageServiceImpl(@Value("${tidb.datasource.url}") String url,
            @Value("${tidb.datasource.username}") String username,
            @Value("${tidb.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public List<Mapping> createStorageSource(SourceDto sourceDto) {
        String tableName = sourceDto.getTableName();
        Map<String, String> schemaMap = new LinkedHashMap<>();
        schemaMap.put("_id_", "text");
        for (Mapping mapping : sourceDto.getMapping()) {
            schemaMap.put(mapping.getFieldMapping(), mapping.getFieldType());
        }

        try (TidbWriter tidbWriter = new TidbWriter(url, username, password)) {
            tidbWriter.createTable(tableName, schemaMap);
        } catch (Exception e) {
            log.error("Create table failed! {}", e.getMessage());
        }

        return sourceDto.getMapping();
    }

    @Override
    public void updateSourceData(SourceDto sourceDto, Map<String, Object> data) {
        String tableName = sourceDto.getTableName();

        try (TidbWriter tidbWriter = new TidbWriter(url, username, password)) {
            tidbWriter.update(tableName, data.get("_id_").toString(), data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Update source data failed! {}", e.getMessage());
            throw new RuntimeException("Update source data failed! " + e.getMessage());
        }
    }

    @Override
    public void batchInsert(SourceDto sourceDto, List<JSONObject> data) {
        log.info("Batch inserting {} records into {}", data.size(), sourceDto.getTableName());

        String tableName = sourceDto.getTableName();
        List<Mapping> mappings = sourceDto.getMapping();

        try (TidbWriter tidbWriter = new TidbWriter(url, username, password)) {
            tidbWriter.batchInsert(tableName, mappings, data);
        } catch (Exception e) {
            log.error("Batch inserting failed! {}", e.getMessage());
        }

    }

    @Override
    public List<JSONObject> getPreviewData(SourceDto sourceDto, String search, String searchBy, Integer page,
            Integer limit) {
        String tableName = sourceDto.getTableName();
        List<Mapping> mappings = sourceDto.getMapping();

        try (TidbReader tidbReader = new TidbReader(url, username, password)) {
            return tidbReader.getPreviewData(tableName, mappings, search, searchBy, page, limit);
        } catch (Exception e) {
            log.error("Get preview data failed! {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<JSONObject> getPreviewDataByQuery(String sqlQuery) {
        try (TidbReader tidbReader = new TidbReader(url, username, password)) {
            return tidbReader.getPreviewDataByQuery(sqlQuery);
        } catch (Exception e) {
            log.error("Get preview data by query failed! {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void cloneTable(String sourceTable, String targetTable) {
        log.info("Cloning table {} to {}", sourceTable, targetTable);
        
        // Standard method with schema creation and data copying
        try (TidbReader tidbReader = new TidbReader(url, username, password);
             TidbWriter tidbWriter = new TidbWriter(url, username, password)) {
            
            // Step 1: Get the schema of the source table
            Map<String, String> schema = getTableSchema(tidbReader, sourceTable);

            // Step 2: Drop the target table if it exists
            tidbWriter.dropTable(targetTable);
            log.info("Dropped target table {}", targetTable);
            
            // Step 3: Create the target table with the same schema
            tidbWriter.createTable(targetTable, schema);
            log.info("Created target table {} with schema: {}", targetTable, schema);
            
            // Step 4: Copy data from source to target
            copyTableData(tidbReader, tidbWriter, sourceTable, targetTable);
            log.info("Successfully cloned table {} to {}", sourceTable, targetTable);
            
        } catch (Exception e) {
            log.error("Failed to clone table {} to {}: {}", sourceTable, targetTable, e.getMessage());
            throw new RuntimeException("Failed to clone table: " + e.getMessage(), e);
        }
    }

    private Map<String, String> getTableSchema(TidbReader tidbReader, String tableName) throws Exception {
        Map<String, String> schema = new LinkedHashMap<>();

        // Get table schema using DESCRIBE command
        String describeSql = "DESCRIBE " + tableName;
        CustomResultSet resultSet = tidbReader.executeQuery(describeSql);
        List<Map<String, Object>> columns = resultSet.getRows();

        for (Map<String, Object> column : columns) {
            String fieldName = column.get("Field").toString();
            String fieldType = column.get("Type").toString();

            // Convert TiDB types to our internal types
            String internalType = convertToInternalType(fieldType);
            schema.put(fieldName, internalType);
        }

        return schema;
    }

    private String convertToInternalType(String tidbType) {
        String lowerType = tidbType.toLowerCase();

        if (lowerType.contains("varchar") || lowerType.contains("char") ||
                lowerType.contains("text") || lowerType.contains("json")) {
            return "text";
        } else if (lowerType.contains("int") || lowerType.contains("bigint") ||
                lowerType.contains("smallint") || lowerType.contains("tinyint")) {
            return "bigint";
        } else if (lowerType.contains("decimal") || lowerType.contains("float") ||
                lowerType.contains("double")) {
            return "decimal";
        } else if (lowerType.contains("date") || lowerType.contains("datetime") ||
                lowerType.contains("timestamp")) {
            return "datetime";
        } else if (lowerType.contains("boolean") || lowerType.contains("bool")) {
            return "boolean";
        } else {
            return "text"; // Default to text for unknown types
        }
    }

    private void copyTableData(TidbReader tidbReader, TidbWriter tidbWriter, 
                              String sourceTable, String targetTable) throws Exception {
        // Method 1: Use INSERT INTO SELECT for faster data copying
        try {
            String insertIntoSelect = "INSERT INTO " + targetTable + " SELECT * FROM " + sourceTable;
            tidbWriter.executeUpdate(insertIntoSelect);
            log.info("Successfully copied data from {} to {} using INSERT INTO SELECT", sourceTable, targetTable);
            return;
        } catch (Exception e) {
            log.warn("INSERT INTO SELECT failed, falling back to batch insert: {}", e.getMessage());
        }
        
        // Method 2: Fallback to batch insert with pagination for large tables
        copyTableDataWithPagination(tidbReader, tidbWriter, sourceTable, targetTable);
    }
    
    private void copyTableDataWithPagination(TidbReader tidbReader, TidbWriter tidbWriter,
                                           String sourceTable, String targetTable) throws Exception {
        final int BATCH_SIZE = 500; // Process 500 rows at a time
        int offset = 0;
        int totalCopied = 0;
        
        while (true) {
            // Get data in batches
            String selectSql = String.format("SELECT * FROM %s LIMIT %d OFFSET %d", 
                                           sourceTable, BATCH_SIZE, offset);
            CustomResultSet resultSet = tidbReader.executeQuery(selectSql);
            List<Map<String, Object>> rows = resultSet.getRows();
            
            if (rows.isEmpty()) {
                break; // No more data to copy
            }
            
            // Convert to JSONObject format for batch insert
            List<JSONObject> jsonData = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                JSONObject jsonRow = new JSONObject();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    jsonRow.put(entry.getKey(), entry.getValue());
                }
                jsonData.add(jsonRow);
            }
            
            // Create mappings for the target table (only once)
            if (offset == 0) {
                List<Mapping> mappings = new ArrayList<>();
                for (String columnName : rows.get(0).keySet()) {
                    if (!"_id_".equals(columnName)) {
                        Mapping mapping = new Mapping();
                        mapping.setFieldMapping(columnName);
                        mapping.setFieldType("text"); // Default type for cloned data
                        mappings.add(mapping);
                    }
                }
                
                // Batch insert data to target table
                tidbWriter.batchInsert(targetTable, mappings, jsonData);
            } else {
                // For subsequent batches, reuse the same mapping structure
                List<Mapping> mappings = new ArrayList<>();
                for (String columnName : rows.get(0).keySet()) {
                    if (!"_id_".equals(columnName)) {
                        Mapping mapping = new Mapping();
                        mapping.setFieldMapping(columnName);
                        mapping.setFieldType("text");
                        mappings.add(mapping);
                    }
                }
                tidbWriter.batchInsert(targetTable, mappings, jsonData);
            }
            
            totalCopied += rows.size();
            offset += BATCH_SIZE;
            
            log.info("Copied batch of {} rows, total: {} rows from {} to {}", 
                    rows.size(), totalCopied, sourceTable, targetTable);
        }
        
        log.info("Completed copying {} total rows from {} to {}", totalCopied, sourceTable, targetTable);
    }

}
