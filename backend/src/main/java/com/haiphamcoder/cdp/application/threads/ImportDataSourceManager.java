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
    private final ImportDataThreadFactory importDataThreadFactory;

    public ImportDataSourceManager(ImportDataThreadFactory importDataThreadFactory) {
        this.importDataThreadFactory = importDataThreadFactory;

        ExecutorService executor = ThreadPool.builder()
                .setCoreSize(Runtime.getRuntime().availableProcessors())
                .setQueueSize(Runtime.getRuntime().availableProcessors() * 2)
                .setNamePrefix("import-data-thread-")
                .setDaemon(true)
                .build()
                .getExecutorService();

        this.taskManager = new TaskManager(1, executor);
    }

    public boolean submit(String userId, Long sourceId) {

        AbstractProcessingThread task = importDataThreadFactory.getThreadImportData(sourceId);

        if (taskManager.trySubmit(task) == null) {
            log.info("max queue size");
            return false;
        }
        return true;
    }
}
