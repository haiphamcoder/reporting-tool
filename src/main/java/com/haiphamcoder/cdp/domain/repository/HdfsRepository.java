package com.haiphamcoder.cdp.domain.repository;

import org.apache.hadoop.fs.FileSystem;

public interface HdfsRepository {
    FileSystem getFileSystem();

    String getNameNode();
}
