package com.haiphamcoder.storage.service;

import java.util.List;

import org.json.JSONObject;

import com.haiphamcoder.storage.domain.dto.SourceDto;
import com.haiphamcoder.storage.domain.dto.SourceDto.Mapping;

public interface StorageService {

    void batchInsert(SourceDto sourceDto, List<JSONObject> data);

    List<Mapping> createStorageSource(SourceDto sourceDto);

}
