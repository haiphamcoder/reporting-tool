package com.haiphamcoder.reporting.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.application.service.ExcelProcessingService;
import com.haiphamcoder.reporting.application.service.HdfsFileService;
import com.haiphamcoder.reporting.shared.processing.ExcelFileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelProcessingServiceImpl implements ExcelProcessingService {
    private final HdfsFileService hdfsFileService;

    @Override
    public List<String> getSchema(String userId, String fileName) {
        return ExcelFileUtils.getHeader(hdfsFileService.streamFile(fileName), fileName);
    }

}
