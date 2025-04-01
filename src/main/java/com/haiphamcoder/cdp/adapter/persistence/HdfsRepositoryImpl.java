package com.haiphamcoder.cdp.adapter.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
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

        return hdfsProperties.getRootFolder() + "/" + fileName;
    }

    @Override
    public Object downloadFile(String filePath) {

        try {
            FSDataInputStream dataInputStream = getFileSystem().open(new Path(filePath));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            IOUtils.copyBytes(dataInputStream, stream, 4096);
            return stream.toByteArray();
        } catch (IOException e) {
            log.error("Error download file from hdfs", e);
        }
        return null;
    }

    @Override
    public InputStream streamFile(String userId, String fileName) {
        String hdfsFolder = this.defaultFS + hdfsProperties.getRootFolder();
        String filePath = hdfsFolder + "/" + userId + "/" + fileName;
        try {
            FSDataInputStream dataInputStream = this.fileSystem.open(new Path(filePath));
            return dataInputStream;
        } catch (IOException e) {
            log.error("Error stream file from hdfs", e);
        }
        return null;
    }
}
