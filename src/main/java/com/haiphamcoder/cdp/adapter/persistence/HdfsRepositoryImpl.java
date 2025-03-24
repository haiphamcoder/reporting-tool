package com.haiphamcoder.cdp.adapter.persistence;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
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

    public HdfsRepositoryImpl(@Value("${hdfs.remote.user}") String remoteUser) {
        if (!Strings.isNullOrEmpty(remoteUser)) {
            System.setProperty("HADOOP_USER_NAME", remoteUser);
        }

        this.configuration = new Configuration();
        this.configuration.addResource(HdfsConfiguration.hdfsSiteConfiguration());
        this.configuration.addResource(HdfsConfiguration.coreSiteConfiguration());
        this.defaultFs = this.configuration.get("fs.defaultFS");
    }

    @Override
    public FileSystem getFileSystem() {
        try {
            fileSystem = FileSystem.get(configuration);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileSystem;
    }

    @Override
    public String getNameNode() {
        return defaultFs;
    }

}
