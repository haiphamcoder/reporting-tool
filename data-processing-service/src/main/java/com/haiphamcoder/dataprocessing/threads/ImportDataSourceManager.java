package com.haiphamcoder.dataprocessing.threads;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.SourceGrpcClient;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.shared.concurrent.TaskManager;
import com.haiphamcoder.dataprocessing.shared.concurrent.ThreadPool;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImportDataSourceManager {
    private final TaskManager taskManager;
    private final StorageService storageService;
    private final SourceGrpcClient sourceGrpcClient;
    private final ImportDataThreadFactory importDataThreadFactory;

    public ImportDataSourceManager(SourceGrpcClient sourceGrpcClient,
            StorageService storageService,
            ImportDataThreadFactory importDataThreadFactory) {
        this.sourceGrpcClient = sourceGrpcClient;
        this.storageService = storageService;
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

    public boolean submit(AbstractProcessingThread task) {

        if (taskManager.trySubmit(task) == null) {
            log.info("max queue size");
            return false;
        }
        return true;
    }

    public boolean submit(Long sourceId, boolean isFirstTime) {
        SourceDto source = sourceGrpcClient.getSourceById(sourceId);
        if (source != null) {
            List<Mapping> schema = source.getMapping();

            if (schema == null || (schema != null && schema.isEmpty())) {
                throw new RuntimeException("Schema is empty");
            }

            if (isFirstTime) {
                storageService.createStorageSource(source);
            }

            AbstractProcessingThread task = importDataThreadFactory.getThreadImportData(source);
            if (taskManager.trySubmit(task) == null) {
                log.info("Submit task failed! Max queue size");
                try {
                    task.shutdown(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("Shutdown task failed! {}", e.getMessage());
                }
                return false;
            } else {
                return true;
            }
        } else {
            throw new RuntimeException("Source not found");
        }
    }
}
