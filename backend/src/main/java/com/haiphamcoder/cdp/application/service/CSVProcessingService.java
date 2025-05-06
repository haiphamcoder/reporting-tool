package com.haiphamcoder.cdp.application.service;

import com.haiphamcoder.cdp.domain.model.PreviewData;

public interface CSVProcessingService extends HdfsFileProcessingService {

    public PreviewData getPreviewData(String userId, String filePath, Integer limit);

}
