package com.haiphamcoder.reporting.application.service;

import java.util.Map;

public interface RawDataService {

    public boolean submit(Long sourceId, boolean isFirstTime);

    public void createTable(Long userId, Long sourceId, Map<String, String> columnMap);

}
