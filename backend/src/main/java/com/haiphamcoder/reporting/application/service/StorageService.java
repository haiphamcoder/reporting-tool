package com.haiphamcoder.reporting.application.service;

import java.util.List;

import org.json.JSONObject;

import com.haiphamcoder.reporting.adapter.dto.SourceDto;
import com.haiphamcoder.reporting.adapter.dto.SourceDto.Mapping;

public interface StorageService {

    void batchInsert(SourceDto sourceDto, List<JSONObject> data);

    List<Mapping> createStorageSource(SourceDto sourceDto);

}
