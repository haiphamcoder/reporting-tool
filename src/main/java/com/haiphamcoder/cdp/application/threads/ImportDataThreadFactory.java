package com.haiphamcoder.cdp.application.threads;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.application.threads.impl.CSVProcessingThread;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.repository.HdfsRepository;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;
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
    private final SourceRepository sourceRepository;

    public AbstractProcessingThread getThreadImportData(Long sourceId) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            JsonNode sourceConfig = source.get().getConfig();

            switch (source.get().getConnectorType()) {
                case CommonConstants.CONNECTOR_TYPE_CSV: {
                    String fileUrl = sourceConfig.get("path").asText(null);
                    if (StringUtils.isNullOrEmpty(fileUrl)) {
                        throw new RuntimeException("File url is required");
                    }
                    return new CSVProcessingThread(storageService, hdfsRepository, fileUrl);
                }

                default:
                    break;
            }
        }
        throw new RuntimeException("Source not found");
    }
}
