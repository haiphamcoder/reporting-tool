package com.haiphamcoder.cdp.application.service.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.infrastructure.tidb.impl.write.TidbWriter;

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
    public void saveBatch(List<JSONObject> data) {
        log.info("Saving batch of {} records", data.size());
        // for (JSONObject record: data) {
        // log.info("Saving record: {}", record);
        // }
    }

    @Override
    public void saveData(JSONObject record) {

    }

    @Override
    public List<Mapping> createStorageSource(SourceDto sourceDto) {
        String tableName = sourceDto.getTableName();
        Map<String, String> schemaMap = new LinkedHashMap<>();
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
    public void batchInsert(SourceDto sourceDto, List<JSONObject> data) {
        log.info("Batch inserting {} records into {}", data.size(), sourceDto.getTableName());

        String tableName = sourceDto.getTableName();
        List<String> columns = new LinkedList<>();
        for (Mapping mapping : sourceDto.getMapping()) {
            columns.add(mapping.getFieldMapping());
        }
        
        try (TidbWriter tidbWriter = new TidbWriter(url, username, password)) {
            tidbWriter.batchInsert(tableName, columns, data);
        } catch (Exception e) {
            log.error("Batch inserting failed! {}", e.getMessage());
        }

    }

}
