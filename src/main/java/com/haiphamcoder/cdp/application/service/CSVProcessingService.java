package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.shared.processing.CSVFileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVProcessingService implements HdfsFileProcessingService {
    private final HdfsFileService hdfsFileService;

    @Override
    public List<String> getSchema(String userId, String fileName) {
        return CSVFileUtils.getHeader(hdfsFileService.streamFile(userId, fileName));
    }

    @Override
    public Stream<List<?>> getRecords(String userId, String fileName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecords'");
    }

    @Override
    public Stream<List<?>> getRecords(String userId, String fileName, int skip, int limit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecords'");
    }

    
}
