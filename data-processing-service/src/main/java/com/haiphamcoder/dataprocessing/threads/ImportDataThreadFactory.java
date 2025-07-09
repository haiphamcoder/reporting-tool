package com.haiphamcoder.dataprocessing.threads;

import org.springframework.stereotype.Component;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.HdfsFileService;
import com.haiphamcoder.dataprocessing.service.SourceGrpcClient;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.threads.impl.CSVProcessingThread;
import com.haiphamcoder.dataprocessing.threads.impl.ExcelProcessingThread;
import com.haiphamcoder.dataprocessing.config.CommonConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportDataThreadFactory {

    private final StorageService storageService;
    private final HdfsFileService hdfsFileService;
    private final SourceGrpcClient sourceGrpcClient;

    public AbstractProcessingThread getThreadImportData(Long userId, SourceDto sourceDto) {

        switch (sourceDto.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV: {
                return new CSVProcessingThread(userId, sourceDto, storageService, hdfsFileService, sourceGrpcClient);
            }

            case CommonConstants.CONNECTOR_TYPE_EXCEL: {
                return new ExcelProcessingThread(userId, sourceDto, storageService, hdfsFileService, sourceGrpcClient);
            }

            default:
                break;
        }
        throw new RuntimeException("Unsupported connector type");
    }
}
