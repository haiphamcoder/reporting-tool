package com.haiphamcoder.dataprocessing.repository;

import java.io.InputStream;
import java.util.Map;

public interface HdfsRepository {

    String uploadFile(String userId, InputStream inputStream, String fileName);

    Object downloadFile(String fileUrl);

    InputStream streamFile(String fileUrl);

    Map<String, String> getHistoryUploadFile(String userId, Integer connectorType);
}
