package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.model.PreviewData;

public interface CSVProcessingService extends HdfsFileProcessingService {

    public PreviewData getPreviewData(String userId, String filePath, Integer limit);

    public List<Mapping> getSchema(Source source);

}
