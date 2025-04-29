package com.haiphamcoder.cdp.application.service.impl;

import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService{

    @Override
    public void saveBatch(List<JSONObject> data) {
        log.info("Saving batch of {} records", data.size());
//        for (JSONObject record: data) {
//            log.info("Saving record: {}", record);
//        }
    }
    
}
