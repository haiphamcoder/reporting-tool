package com.haiphamcoder.cdp.infrastructure.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;

public class HdfsConfiguration {
    private static final String HDFS_ENV_KEY = "hdfs.site.conf";
    private static final String CORE_ENV_KEY = "core.site.conf";

    public static Configuration hdfsSiteConfiguration() {
        return load(HDFS_ENV_KEY, "hdfs-site.xml");
    }

    public static Configuration coreSiteConfiguration() {
        return load(CORE_ENV_KEY, "core-site.xml");
    }

    private static Configuration load(String envKey, String defaultFileInClassPath) {
        final String path = System.getProperty(envKey);
        if (path != null) return loadConfig(path);
        final ClassLoader cl = HdfsConfiguration.class.getClassLoader();
        return loadConfigStream(cl.getResourceAsStream(defaultFileInClassPath));
    }

    private static Configuration loadConfig(String path) {
        try (InputStream is = new FileInputStream(path)) {
            return loadConfigStream(is);
        } catch (IOException ignored) {
            return new Configuration();
        }
    }

    private static Configuration loadConfigStream(InputStream is) {
        Configuration configuration = new Configuration();
        try {
            configuration.addResource(is);
        } catch (Exception ignored) {
        }
        return configuration;
    }
}
