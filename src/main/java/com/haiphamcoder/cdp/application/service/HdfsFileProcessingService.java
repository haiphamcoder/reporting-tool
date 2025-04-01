package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.stream.Stream;

public interface HdfsFileProcessingService {
    List<String> getSchema(String userId, String fileName);

    Stream<List<?>> getRecords(String userId, String fileName);

    Stream<List<?>> getRecords(String userId, String fileName, int skip, int limit);
} 