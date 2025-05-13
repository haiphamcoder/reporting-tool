package com.haiphamcoder.cdp.application.service;

import java.util.List;

import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.model.PreviewData;

public interface CSVProcessingService extends HdfsFileProcessingService {

    public PreviewData getPreviewData(String userId, String filePath, Integer limit);

    public List<Mapping> getSchema(Source source);

}
