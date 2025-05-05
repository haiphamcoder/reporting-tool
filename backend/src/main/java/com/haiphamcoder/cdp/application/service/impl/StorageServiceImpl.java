package com.haiphamcoder.cdp.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.domain.repository.TiDBRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final TiDBRepository tidbRepository;

    @Override
    public void saveBatch(List<JSONObject> data) {
        log.info("Saving batch of {} records", data.size());
        // for (JSONObject record: data) {
        // log.info("Saving record: {}", record);
        // }
    }

    @Override
    public void saveData(JSONObject record) {
        log.info("Saving record: {}", record);
        String tableName = record.getString("table_name");
        List<String> schema = record.getJSONArray("schema").toList().stream().map(Object::toString)
                .collect(Collectors.toList());
        List<String> values = record.getJSONArray("values").toList().stream().map(Object::toString)
                .collect(Collectors.toList());
        tidbRepository.insertData(tableName, schema, values);
    }

}
