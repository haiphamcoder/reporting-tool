package com.haiphamcoder.cdp.application.threads;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.cdp.application.service.SourceService;
import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.application.threads.impl.CSVProcessingThread;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.repository.HdfsRepository;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportDataThreadFactory {

    private final StorageService storageService;
    private final HdfsRepository hdfsRepository;
    private final SourceService sourceService;

    public AbstractProcessingThread getThreadImportData(Long sourceId) {
        Source source = sourceService.getSourceById(sourceId);
        JsonNode sourceConfig = source.getConfig();

        switch (source.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV: {
                String fileUrl = sourceConfig.get("path").asText(null);
                if (StringUtils.isNullOrEmpty(fileUrl)) {
                    throw new RuntimeException("File url is required");
                }
                return new CSVProcessingThread(storageService, sourceService, hdfsRepository, fileUrl);
            }

            default:
                break;
        }
        throw new RuntimeException("Unsupported connector type");
    }
}
