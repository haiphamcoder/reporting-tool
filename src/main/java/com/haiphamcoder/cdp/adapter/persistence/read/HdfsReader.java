package com.haiphamcoder.cdp.adapter.persistence.read;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HdfsReader implements AutoCloseable {
    private String fileUri;
    private Configuration configuration;
    private FileSystem fileSystem;

    public HdfsReader(String fileUri, Configuration configuration) {
        this.fileUri = fileUri;
        this.configuration = configuration;
        initializeFileSystem();
    }

    private void initializeFileSystem() {
        try {
            this.fileSystem = FileSystem.get(configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileUrl() {
        return fileSystem.getUri().toString() + fileUri;
    }

    public String readAsString() {
        try {
            return fileSystem.open(new Path(getFileUrl())).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            fileSystem.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}