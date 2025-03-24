package com.haiphamcoder.cdp.adapter.persistence.read;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HdfsReader {
    private String path;

    public HdfsReader(String path) {
        this.path = path;
    }

    public String read() {
        return path;
    }
}
