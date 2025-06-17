package com.haiphamcoder.reporting.repository;

import java.io.InputStream;
import java.util.Map;

public interface HdfsRepository {

    String uploadFile(Long userId, InputStream inputStream, String fileName);

    Object downloadFile(String fileUrl);

    InputStream streamFile(String fileUrl);

    Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType);
}
