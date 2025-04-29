package com.haiphamcoder.cdp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.shared.processing.ExcelFileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelProcessingService implements HdfsFileProcessingService{
    private final HdfsFileService hdfsFileService;

    @Override
    public List<String> getSchema(String userId, String fileName) {
        return ExcelFileUtils.getHeader(hdfsFileService.streamFile(userId, fileName), fileName);
    }
    
}
