package com.haiphamcoder.dataprocessing.service;

import java.io.InputStream;
import java.util.Map;

public interface HdfsFileService {

    public String uploadFile(String userId, InputStream inputStream, String fileName);

    public InputStream streamFile(String fileUrl);

    public Map<String, String> getHistoryUploadFile(String userId, Integer connectorType);

}
