package com.haiphamcoder.storage.threads;

import com.haiphamcoder.storage.shared.concurrent.ShutdownableThread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractProcessingThread extends ShutdownableThread {

    public AbstractProcessingThread(String threadName, Boolean isDaemon) {
        super(threadName, isDaemon);
    }

}
