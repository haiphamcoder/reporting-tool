package com.haiphamcoder.reporting.application.service;

import java.util.List;

public interface HdfsFileProcessingService {
    List<String> getSchema(String userId, String fileName);
} 