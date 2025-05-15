package com.haiphamcoder.cdp.application.threads;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.cdp.adapter.dto.mapper.SourceMapper;
import com.haiphamcoder.cdp.application.service.StorageService;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;
import com.haiphamcoder.cdp.shared.concurrent.TaskManager;
import com.haiphamcoder.cdp.shared.concurrent.ThreadPool;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImportDataSourceManager {
    private final TaskManager taskManager;
    private final SourceRepository sourceRepository;
    private final StorageService storageService;
    private final ImportDataThreadFactory importDataThreadFactory;

    public ImportDataSourceManager(SourceRepository sourceRepository,
            StorageService storageService,
            ImportDataThreadFactory importDataThreadFactory) {
        this.sourceRepository = sourceRepository;
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
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            SourceDto sourceDto = SourceMapper.toDto(source.get());
            List<Mapping> schema = sourceDto.getMapping();
            if (schema != null && !schema.isEmpty() && schema.get(0).getFieldMapping() == null) {
                sourceDto.setMapping(storageService.createStorageSource(sourceDto));
                sourceRepository.updateSource(source.get());
            } else {
                throw new RuntimeException("Schema is empty");
            }

            AbstractProcessingThread task = importDataThreadFactory.getThreadImportData(sourceDto);
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
