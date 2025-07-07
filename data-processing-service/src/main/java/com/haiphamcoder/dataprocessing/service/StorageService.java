package com.haiphamcoder.dataprocessing.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;

public interface StorageService {

    void batchInsert(SourceDto sourceDto, List<JSONObject> data);

    List<Mapping> createStorageSource(SourceDto sourceDto);

    List<JSONObject> getPreviewData(SourceDto sourceDto, String search, String searchBy, Integer page, Integer limit);

    List<JSONObject> getPreviewDataByQuery(String sqlQuery);

    void updateSourceData(SourceDto sourceDto, Map<String, Object> data);

    void cloneTable(String sourceTable, String targetTable);

}
