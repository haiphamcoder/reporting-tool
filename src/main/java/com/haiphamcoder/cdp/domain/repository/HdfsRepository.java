package com.haiphamcoder.cdp.domain.repository;

import java.io.InputStream;

import org.apache.hadoop.fs.FileSystem;

public interface HdfsRepository {
    FileSystem getFileSystem();

    String getNameNode();

    String uploadFile(InputStream inputStream, String fileName);

    Object downloadFile(String filePath);

    InputStream streamFile(String filePath);
}
