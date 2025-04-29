package com.haiphamcoder.cdp.adapter.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.domain.repository.HdfsRepository;
import com.haiphamcoder.cdp.infrastructure.config.properties.HdfsProperties;
import com.haiphamcoder.cdp.shared.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HdfsRepositoryImpl implements HdfsRepository {

    private final Configuration configuration;
    private final String defaultFS;
    private final HdfsProperties hdfsProperties;
    private final FileSystem fileSystem;

    public HdfsRepositoryImpl(@Qualifier("hdfsProperties") HdfsProperties hdfsProperties,
            @Qualifier("hdfsSiteInputStream") InputStream hdfsSiteInputStream,
            @Qualifier("coreSiteInputStream") InputStream coreSiteInputStream) {
        this.hdfsProperties = hdfsProperties;
        if (!StringUtils.isNullOrEmpty(hdfsProperties.getUser())) {
            System.setProperty("HADOOP_USER_NAME", hdfsProperties.getUser());
        }
        this.configuration = new Configuration();
        this.configuration.addResource(hdfsSiteInputStream);
        this.configuration.addResource(coreSiteInputStream);
        this.defaultFS = this.configuration.get("fs.defaultFS");
        this.fileSystem = getFileSystem();
    }

    private FileSystem getFileSystem() {
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(this.configuration);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileSystem;
    }

    @Override
    public String uploadFile(String userId, InputStream inputStream, String fileName) {
        String hdfsFolder = this.defaultFS + hdfsProperties.getRootFolder();
        String fileUpload = hdfsFolder + "/" + userId + "/" + fileName;
        log.info("Uploading file to hdfs: {}", fileUpload);
        try {
            FSDataOutputStream dataOutputStream = this.fileSystem.create(new Path(fileUpload));
            IOUtils.copyBytes(inputStream, dataOutputStream, 4096, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileUpload;
    }

    @Override
    public Object downloadFile(String fileUrl) {

        try {
            FSDataInputStream dataInputStream = getFileSystem().open(new Path(fileUrl));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            IOUtils.copyBytes(dataInputStream, stream, 4096);
            return stream.toByteArray();
        } catch (IOException e) {
            log.error("Error download file from hdfs", e);
        }
        return null;
    }

    @Override
    public InputStream streamFile(String fileUrl) {
        try {
            FSDataInputStream dataInputStream = this.fileSystem.open(new Path(fileUrl));
            return dataInputStream;
        } catch (IOException e) {
            log.error("Error stream file from hdfs", e);
        }
        return null;
    }

    @Override
    public Map<String, String> getHistoryUploadFile(String userId, Integer connectorType) {
        String hdfsFolder = this.defaultFS + hdfsProperties.getRootFolder();
        String fileUpload = hdfsFolder + "/" + userId + "/" + connectorType;
        log.info("Getting history upload file from hdfs: {}", fileUpload);
        try {
            FileStatus[] fileStatuses = this.fileSystem.listStatus(new Path(fileUpload));
            Map<String, String> historyUploadFile = new HashMap<>();
            for (FileStatus fileStatus : fileStatuses) {
                historyUploadFile.put(fileStatus.getPath().getName(), fileStatus.getPath().toString());
            }
            return historyUploadFile;
        } catch (IOException e) {
            log.error("Error getting history upload file from hdfs", e);
        }
        return Collections.emptyMap();
    }
}
