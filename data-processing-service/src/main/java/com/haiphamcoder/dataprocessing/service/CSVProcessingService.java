package com.haiphamcoder.dataprocessing.service;

import java.util.List;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto.Mapping;
import com.haiphamcoder.dataprocessing.domain.entity.Source;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;

public interface CSVProcessingService extends HdfsFileProcessingService {

    public PreviewData getPreviewData(String userId, String filePath, Integer limit);

    public List<Mapping> getSchema(Source source);

}
