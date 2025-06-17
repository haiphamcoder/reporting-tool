package com.haiphamcoder.reporting.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.haiphamcoder.reporting.config.properties.HdfsProperties;

import java.io.*;

@Getter
@Configuration
public class HdfsConfiguration {

    @Bean(name = "hdfsProperties")
    @ConfigurationProperties(prefix = "hadoop.hdfs")
    HdfsProperties getHdfsProperties() {
        return new HdfsProperties();
    }

    @Bean("hdfsSiteInputStream")
    InputStream getHdfsSiteInputStream(@Qualifier("hdfsProperties") HdfsProperties hdfsProperties)
            throws FileNotFoundException {
        return new FileInputStream(hdfsProperties.getHdfsSiteConf());
    }

    @Bean("coreSiteInputStream")
    InputStream getCoreSiteInputStream(@Qualifier("hdfsProperties") HdfsProperties hdfsProperties)
            throws FileNotFoundException {
        return new FileInputStream(hdfsProperties.getCoreSiteConf());
    }
}
