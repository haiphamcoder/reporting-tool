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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.haiphamcoder.cdp.domain.repository.HdfsRepository;
import com.haiphamcoder.cdp.infrastructure.config.HdfsConfiguration;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HdfsRepositoryImpl implements HdfsRepository {
    private String defaultFs;
    private Configuration configuration;
    private FileSystem fileSystem;

    private final String ROOT_FOLDER;

    public HdfsRepositoryImpl(@Value("${hdfs.remote.user}") String remoteUser,
            @Value("${hdfs.folder.root}") String rootFolder, HdfsConfiguration hdfsConfiguration) {
        ROOT_FOLDER = rootFolder;
        if (!Strings.isNullOrEmpty(remoteUser)) {
            System.setProperty("HADOOP_USER_NAME", remoteUser);
        }

        this.configuration = new Configuration();
        this.configuration.addResource(hdfsConfiguration.hdfsSiteConfiguration());
        this.configuration.addResource(hdfsConfiguration.coreSiteConfiguration());
        this.defaultFs = this.configuration.get("fs.defaultFS");

        // init file system
        try {
            log.info("HDFS file system initializing...");
            this.fileSystem = FileSystem.get(configuration);
            log.info("HDFS file system initialized");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public String getNameNode() {
        return defaultFs;
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        String hdfsFolder = getNameNode() + ROOT_FOLDER;
        String fileUpload = hdfsFolder + "/" + fileName;
        log.info("Uploading file to hdfs: {}", fileUpload);
        try {
            FSDataOutputStream dataOutputStream = fileSystem.create(new Path(fileUpload));
            IOUtils.copyBytes(inputStream, dataOutputStream, 4096, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ROOT_FOLDER + "/" + fileName;
    }

    @Override
    public Object downloadFile(String filePath) {

        try {
            FSDataInputStream dataInputStream = fileSystem.open(new Path(filePath));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            IOUtils.copyBytes(dataInputStream, stream, 4096);
            return stream.toByteArray();
        } catch (IOException e) {
            log.error("Error download file from hdfs", e);
        }
        return null;
    }

    @Override
    public InputStream streamFile(String filePath) {
        try {
            FSDataInputStream dataInputStream = fileSystem.open(new Path(filePath));
            return dataInputStream;
        } catch (IOException e) {
            log.error("Error stream file from hdfs", e);
        }
        return null;
    }

}
