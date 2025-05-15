package com.haiphamcoder.cdp.application.service;

import java.util.List;

import org.json.JSONObject;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;

public interface StorageService {
    void saveBatch(List<JSONObject> data);

    void saveData(JSONObject record);

    void batchInsert(SourceDto sourceDto, List<JSONObject> data);

    List<Mapping> createStorageSource(SourceDto sourceDto);
}
