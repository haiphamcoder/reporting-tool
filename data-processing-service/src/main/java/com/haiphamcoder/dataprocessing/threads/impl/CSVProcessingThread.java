package com.haiphamcoder.dataprocessing.threads.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.HdfsFileService;
import com.haiphamcoder.dataprocessing.threads.AbstractProcessingThread;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.shared.concurrent.ThreadPool;
import com.haiphamcoder.dataprocessing.shared.processing.CSVFileUtils;
import com.haiphamcoder.dataprocessing.shared.processing.HeaderNormalizer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVProcessingThread extends AbstractProcessingThread {
    private final HdfsFileService hdfsFileService;
    private final ExecutorService executorService;
    private final StorageService storageService;
    private final SourceDto sourceDto;

    public CSVProcessingThread(SourceDto sourceDto,
            StorageService storageService,
            HdfsFileService hdfsFileService) {
        super("csv-processing-thread", false);
        this.hdfsFileService = hdfsFileService;
        this.storageService = storageService;
        this.sourceDto = sourceDto;

        executorService = ThreadPool.builder()
                .setCoreSize(Runtime.getRuntime().availableProcessors())
                .setMaxSize(Runtime.getRuntime().availableProcessors() * 2)
                .setQueueSize(100)
                .setNamePrefix("csv-processing-thread")
                .build()
                .getExecutorService();
    }

    protected boolean process() {
        try (InputStream inputStream = hdfsFileService.streamFile(sourceDto.getConfig().get("file_path").asText())) {
            try (CSVReader csvReader = CSVFileUtils.createCSVReader(inputStream)) {
                String[] firstLine = csvReader.readNext();
                List<String> header = firstLine != null ? Arrays.asList(firstLine) : Collections.emptyList();
                if (header.isEmpty()) {
                    log.error("Header is empty");
                    return false;
                }

                int chunkSize = 200;
                int recordCount = 0;
                List<String[]> records = new LinkedList<>();
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    if (record.length != header.size()) {
                        log.error("Record length does not match field names size");
                        continue;
                    }
                    recordCount++;

                    if (records.size() == chunkSize) {
                        final List<String[]> recordsChunk = new LinkedList<>(records);
                        executorService.submit(() -> processChunk(recordsChunk, header));
                        records.clear();
                    }
                    records.add(record);
                }

                if (records.size() > 0) {
                    executorService.submit(() -> processChunk(records, header));
                }

                log.info("Total records: {}", recordCount);
            } catch (CsvValidationException e) {
                throw new RuntimeException(e);
            }
            return true;
        } catch (IOException e) {
            log.error("Error processing CSV", e);
            return false;
        }
    }

    private void processChunk(List<String[]> records, List<String> fieldNames) {
        log.info("Processing chunk of {} records", records.size());
        List<JSONObject> chunkData = new LinkedList<>();
        for (String[] record : records) {
            if (record.length != fieldNames.size()) {
                log.error("Record length does not match field names size");
                continue;
            }

            JSONObject recordJson = new JSONObject();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = HeaderNormalizer.normalize(fieldNames.get(i));
                String value = record[i];
                recordJson.put(fieldName, value);
            }
            chunkData.add(recordJson);
        }

        storageService.batchInsert(sourceDto, chunkData);

        log.info("Chunk of {} records processed", records.size());
    }

    @Override
    public void execute() {
        try {
            log.info("Start processing CSV for source {}", sourceDto.getId());

            if (process()) {
                log.info("CSV of {} processed successfully", sourceDto.getId());
            } else {
                log.error("CSV of {} processed failed", sourceDto.getId());
            }
        } catch (Exception exception) {
            log.error("CSV of {} process error!", sourceDto.getId());
            log.error(exception.getMessage());
        }
    }
}
