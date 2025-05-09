package com.haiphamcoder.cdp.application.threads;

import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.application.service.HdfsFileService;
import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.application.threads.impl.CSVProcessingThread;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportDataThreadFactory {

    private final StorageService storageService;
    private final HdfsFileService hdfsFileService;

    public AbstractProcessingThread getThreadImportData(Source source) {
        String tableName = source.getTableName();

        switch (source.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV: {
                return new CSVProcessingThread(storageService, hdfsFileService, tableName);
            }

            default:
                break;
        }
        throw new RuntimeException("Unsupported connector type");
    }
}
