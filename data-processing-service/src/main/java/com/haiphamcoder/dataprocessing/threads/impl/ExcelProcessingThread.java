package com.haiphamcoder.dataprocessing.threads.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.HdfsFileService;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.shared.StringUtils;
import com.haiphamcoder.dataprocessing.shared.concurrent.ThreadPool;
import com.haiphamcoder.dataprocessing.shared.processing.ExcelFileUtils.ExcelFileFormat;
import com.haiphamcoder.dataprocessing.shared.processing.HeaderNormalizer;
import com.haiphamcoder.dataprocessing.threads.AbstractProcessingThread;
import com.haiphamcoder.dataprocessing.shared.processing.ExcelFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelProcessingThread extends AbstractProcessingThread {
    private final HdfsFileService hdfsFileService;
    private final ExecutorService executorService;
    private final StorageService storageService;
    private final SourceDto sourceDto;

    public ExcelProcessingThread(SourceDto sourceDto,
            StorageService storageService,
            HdfsFileService hdfsFileService) {
        super("excel-processing-thread", false);
        this.hdfsFileService = hdfsFileService;
        this.storageService = storageService;
        this.sourceDto = sourceDto;

        executorService = ThreadPool.builder()
                .setCoreSize(Runtime.getRuntime().availableProcessors())
                .setMaxSize(Runtime.getRuntime().availableProcessors() * 2)
                .setQueueSize(100)
                .setNamePrefix("excel-processing-thread")
                .build()
                .getExecutorService();
    }

    /**
     * Process the Excel file
     * 
     * @return true if the Excel file is processed successfully, false otherwise
     */
    protected boolean process() {

        JsonNode filePathNode = sourceDto.getConfig().get("file_path");
        if (filePathNode == null) {
            log.error("File path is not set");
            return false;
        }
        String filePath = filePathNode.asText();

        JsonNode sheetNameNode = sourceDto.getConfig().get("sheet_name");
        if (sheetNameNode == null) {
            log.error("Sheet name is not set");
            return false;
        }
        String sheetName = sheetNameNode.asText();

        JsonNode dataRangeSelectedNode = sourceDto.getConfig().get("data_range_selected");
        if (dataRangeSelectedNode == null) {
            log.error("Data range selected is not set");
            return false;
        }
        String dataRangeSelected = dataRangeSelectedNode.asText();

        try (InputStream inputStream = hdfsFileService.streamFile(filePath)) {
            ExcelFileFormat fileFormat = ExcelFileUtils.getFileFormat(filePath);
            try (Workbook workbook = ExcelFileUtils.createWorkbook(inputStream, fileFormat)) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new RuntimeException("Sheet not found");
                }

                int[] dataRange = ExcelFileUtils.processFileSelection(sheet, dataRangeSelected);
                int rowsMin = dataRange[0];
                int colsMin = dataRange[1];
                int rowsMax = dataRange[2];
                int colsMax = dataRange[3];

                List<String> header = new LinkedList<>();
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext() && header.isEmpty()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() < rowsMin || row.getRowNum() > rowsMax) {
                        continue;
                    }
                    for (int i = colsMin; i < colsMax; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null && !cell.equals("")) {
                            header.add(ExcelFileUtils.getCellValueAsString(cell));
                        }
                    }
                }

                int chunkSize = 200;
                int recordCount = 0;
                List<String[]> records = new LinkedList<>();
                String[] record;
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() < rowsMin || row.getRowNum() > rowsMax) {
                        continue;
                    }
                    record = new String[colsMax - colsMin];
                    for (int i = colsMin; i < colsMax; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null && !cell.equals("")) {
                            if (cell.getCellStyle().equals(CellType.NUMERIC)
                                    && ExcelFileUtils.getCellValueAsString(cell, evaluator).contains("$")) {
                                CellStyle cellStyle = workbook.createCellStyle();
                                cell.setCellStyle(cellStyle);
                            }
                            String value = ExcelFileUtils.getCellValueAsString(cell, evaluator);
                            if (!StringUtils.isNullOrEmpty(value)) {
                                record[i - colsMin] = value;
                            } else {
                                record[i - colsMin] = null;
                            }
                        }
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
            } catch (IOException e) {
                log.error("Error processing Excel", e);
                return false;
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
            log.info("Start processing Excel for source {}", sourceDto.getId());

            if (process()) {
                log.info("Excel of {} processed successfully", sourceDto.getId());
            } else {
                log.error("Excel of {} processed failed", sourceDto.getId());
            }
        } catch (Exception exception) {
            log.error("Excel of {} process error!", sourceDto.getId());
            log.error(exception.getMessage());
        }
    }
}
