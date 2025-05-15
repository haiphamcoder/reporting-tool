package com.haiphamcoder.cdp.application.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.RawDataService;
import com.haiphamcoder.cdp.application.threads.ImportDataSourceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawDataServiceImpl implements RawDataService {

    private final ImportDataSourceManager importDataSourceManager;

    @Override
    public boolean submit(Long sourceId, boolean isFirstTime) {
        return importDataSourceManager.submit(sourceId, isFirstTime);
    }

    @Override
    public void createTable(Long userId, Long sourceId, Map<String, String> columnMap) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTable'");
    }
}
