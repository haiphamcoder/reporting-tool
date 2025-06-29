package com.haiphamcoder.dataprocessing.service;

import java.util.List;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;

public interface CSVProcessingService extends HdfsFileProcessingService {

    public PreviewData getPreviewData(String userId, String filePath, Integer limit);

    public List<Mapping> getSchema(SourceDto source);

    public List<String> getSchema(String userId, String fileName);

}
