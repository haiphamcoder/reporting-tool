package com.haiphamcoder.dataprocessing.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto.Mapping;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.infrastructure.tidb.impl.read.TidbReader;
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
        List<Mapping> mappings = sourceDto.getMapping();
        
        try (TidbWriter tidbWriter = new TidbWriter(url, username, password)) {
            tidbWriter.batchInsert(tableName, mappings, data);
        } catch (Exception e) {
            log.error("Batch inserting failed! {}", e.getMessage());
        }

    }

    @Override
    public List<JSONObject> getPreviewData(SourceDto sourceDto, Integer limit) {
        String tableName = sourceDto.getTableName();
        List<Mapping> mappings = sourceDto.getMapping();

        try (TidbReader tidbReader = new TidbReader(url, username, password)) {
            return tidbReader.getPreviewData(tableName, mappings, limit);
        } catch (Exception e) {
            log.error("Get preview data failed! {}", e.getMessage());
            return new ArrayList<>();
        }
    }

}
