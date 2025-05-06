package com.haiphamcoder.cdp.application.threads;

import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.shared.concurrent.TaskManager;
import com.haiphamcoder.cdp.shared.concurrent.ThreadPool;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImportDataSourceManager {
    private final TaskManager taskManager;

    public ImportDataSourceManager() {

        ExecutorService executor = ThreadPool.builder()
                .setCoreSize(Runtime.getRuntime().availableProcessors())
                .setQueueSize(Runtime.getRuntime().availableProcessors() * 2)
                .setNamePrefix("import-data-thread-")
                .setDaemon(true)
                .build()
                .getExecutorService();

        this.taskManager = new TaskManager(1, executor);
    }

    public boolean submit(AbstractProcessingThread task) {

        if (taskManager.trySubmit(task) == null) {
            log.info("max queue size");
            return false;
        }
        return true;
    }
}
