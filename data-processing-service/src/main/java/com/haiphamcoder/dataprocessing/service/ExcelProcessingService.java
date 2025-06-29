package com.haiphamcoder.dataprocessing.service;

import java.util.List;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;

public interface ExcelProcessingService extends HdfsFileProcessingService {

    public PreviewData getPreviewData(String userId, String filePath, String sheetName, String dataRangeSelected, Integer limit);

    public List<Mapping> getSchema(SourceDto source);

    public List<String> getSchema(String userId, String fileName, String sheetName, String dataRangeSelected);

    public List<String> getSheets(Long userId, String filePath);

}
