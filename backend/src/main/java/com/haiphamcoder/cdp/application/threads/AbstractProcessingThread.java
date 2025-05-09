package com.haiphamcoder.cdp.application.threads;

import com.haiphamcoder.cdp.application.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractProcessingThread implements Runnable {

    protected final StorageService storageService;

    public AbstractProcessingThread(StorageService storageService) {
        this.storageService = storageService;
    }

    protected abstract boolean process();

    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

}
