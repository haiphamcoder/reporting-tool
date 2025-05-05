package com.haiphamcoder.cdp.application.service;

import java.util.List;

import org.json.JSONObject;

public interface StorageService {
    void saveBatch(List<JSONObject> data);

    void saveData(JSONObject record);
}
