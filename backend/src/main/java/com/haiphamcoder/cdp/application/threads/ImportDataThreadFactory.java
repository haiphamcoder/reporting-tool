package com.haiphamcoder.cdp.application.threads;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.cdp.application.service.HdfsFileService;
import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.application.threads.impl.CSVProcessingThread;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.StringUtils;

public class ImportDataThreadFactory {

    public static AbstractProcessingThread getThreadImportData(Source source, StorageService storageService,
            HdfsFileService hdfsFileService) {
        JsonNode sourceConfig = source.getConfig();

        switch (source.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV: {
                String fileUrl = sourceConfig.get("path").asText(null);
                if (StringUtils.isNullOrEmpty(fileUrl)) {
                    throw new RuntimeException("File url is required");
                }
                return new CSVProcessingThread(storageService, hdfsFileService, fileUrl);
            }

            default:
                break;
        }
        throw new RuntimeException("Unsupported connector type");
    }
}
