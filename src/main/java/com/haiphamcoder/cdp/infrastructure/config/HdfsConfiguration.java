package com.haiphamcoder.cdp.infrastructure.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.springframework.stereotype.Component;

@Component
public class HdfsConfiguration {
    private final String HDFS_ENV_KEY = "hdfs.site.conf";
    private final String CORE_ENV_KEY = "core.site.conf";

    public Configuration hdfsSiteConfiguration() {
        return load(HDFS_ENV_KEY, "hdfs-site.xml");
    }

    public Configuration coreSiteConfiguration() {
        return load(CORE_ENV_KEY, "core-site.xml");
    }

    private Configuration load(String envKey, String defaultFileInClassPath) {
        final String path = System.getProperty(envKey);
        if (path != null) return loadConfig(path);
        final ClassLoader cl = HdfsConfiguration.class.getClassLoader();
        return loadConfigStream(cl.getResourceAsStream(defaultFileInClassPath));
    }

    private Configuration loadConfig(String path) {
        try (InputStream is = new FileInputStream(path)) {
            return loadConfigStream(is);
        } catch (IOException ignored) {
            return new Configuration();
        }
    }

    private Configuration loadConfigStream(InputStream is) {
        Configuration configuration = new Configuration();
        try {
            configuration.addResource(is);
        } catch (Exception ignored) {
        }
        return configuration;
    }
}
