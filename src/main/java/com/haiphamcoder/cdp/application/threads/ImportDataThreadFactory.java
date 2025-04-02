package com.haiphamcoder.cdp.application.threads;

import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.application.threads.impl.CSVProcessingThread;
import com.haiphamcoder.cdp.domain.repository.HdfsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportDataThreadFactory {

    private final StorageService storageService;
    private final HdfsRepository hdfsRepository;

    public AbstractProcessingThread getThreadImportData(String fileUrl,boolean isFirstTime) {
        return new CSVProcessingThread(storageService, hdfsRepository, fileUrl);
    }
}
