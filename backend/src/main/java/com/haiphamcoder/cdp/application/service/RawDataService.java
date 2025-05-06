package com.haiphamcoder.cdp.application.service;

import java.util.Map;

public interface RawDataService {

    public void createTable(Long userId, Long sourceId, Map<String, String> columnMap);

}
