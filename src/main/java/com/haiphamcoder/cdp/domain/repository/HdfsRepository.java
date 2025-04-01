package com.haiphamcoder.cdp.domain.repository;

import java.io.InputStream;

public interface HdfsRepository {

    String uploadFile(String userId, InputStream inputStream, String fileName);

    Object downloadFile(String filePath);

    InputStream streamFile(String filePath);
}
