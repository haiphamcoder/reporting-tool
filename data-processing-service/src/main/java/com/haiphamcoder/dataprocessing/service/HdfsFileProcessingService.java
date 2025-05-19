package com.haiphamcoder.dataprocessing.service;

import java.util.List;

public interface HdfsFileProcessingService {
    List<String> getSchema(String userId, String fileName);
} 