package com.haiphamcoder.storage.threads;

import org.springframework.stereotype.Component;

import com.haiphamcoder.storage.adapter.dto.SourceDto;
import com.haiphamcoder.storage.application.service.HdfsFileService;
import com.haiphamcoder.storage.application.service.StorageService;
import com.haiphamcoder.storage.application.threads.impl.CSVProcessingThread;
import com.haiphamcoder.storage.infrastructure.config.CommonConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportDataThreadFactory {

    private final StorageService storageService;
    private final HdfsFileService hdfsFileService;

    public AbstractProcessingThread getThreadImportData(SourceDto sourceDto) {

        switch (sourceDto.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV: {
                return new CSVProcessingThread(sourceDto, storageService, hdfsFileService);
            }

            default:
                break;
        }
        throw new RuntimeException("Unsupported connector type");
    }
}
