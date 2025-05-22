package com.haiphamcoder.reporting.service;

import java.io.InputStream;
import java.util.Map;

public interface HdfsFileService {

    public String uploadFile(Long userId, InputStream inputStream, String fileName);

    public InputStream streamFile(String fileUrl);

    public Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType);

}
