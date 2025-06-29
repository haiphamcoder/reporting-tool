package com.haiphamcoder.dataprocessing.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.service.ExcelProcessingService;
import com.haiphamcoder.dataprocessing.service.HdfsFileService;
import com.haiphamcoder.dataprocessing.shared.StringUtils;
import com.haiphamcoder.dataprocessing.shared.TidbDataTypeDetector;
import com.haiphamcoder.dataprocessing.shared.processing.ExcelFileUtils;
import com.haiphamcoder.dataprocessing.shared.processing.ExcelFileUtils.ExcelFileFormat;
import com.haiphamcoder.dataprocessing.shared.processing.HeaderNormalizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelProcessingServiceImpl implements ExcelProcessingService {
    private final HdfsFileService hdfsFileService;

    @Override
    public PreviewData getPreviewData(String userId, String filePath, String sheetName, String dataRangeSelected,
            Integer limit) {
        PreviewData previewData = new PreviewData();

        Map<Integer, String> schemaMap = new HashMap<>();

        try (InputStream inputStream = hdfsFileService.streamFile(filePath)) {
            ExcelFileFormat fileFormat = ExcelFileUtils.getFileFormat(filePath);
            try (Workbook workbook = ExcelFileUtils.createWorkbook(inputStream, fileFormat)) {
                Sheet sheet = workbook.getSheet(sheetName);

                if (sheet == null) {
                    throw new RuntimeException("Sheet not found");
                }

                Iterator<Row> rowIterator = sheet.rowIterator();

                while (rowIterator.hasNext() && previewData.getRecords().size() < limit) {
                    Row row = rowIterator.next();
                    if (schemaMap.isEmpty()) {
                        schemaMap.put(row.getRowNum(), row.getCell(0).getStringCellValue());
                    }
                }

            }
        } catch (IOException e) {
            log.error("Get file on server error");
            e.printStackTrace();
            throw new RuntimeException("Get file on server error");
        }

        return previewData;

    }

    @Override
    public List<Mapping> getSchema(SourceDto source) {
        log.info("Source: {}", source);

        ObjectNode config = source.getConfig();

        if (config == null) {
            throw new RuntimeException("Source config is null");
        }

        JsonNode filePathNode = config.get("file_path");
        if (filePathNode == null) {
            throw new RuntimeException("File path is null");
        }
        String filePath = filePathNode.asText();
        if (StringUtils.isNullOrEmpty(filePath)) {
            throw new RuntimeException("File path is null or empty");
        }

        JsonNode sheetNameNode = config.get("sheet_name");
        if (sheetNameNode == null) {
            throw new RuntimeException("Sheet name is null");
        }
        String sheetName = sheetNameNode.asText();
        if (StringUtils.isNullOrEmpty(sheetName)) {
            throw new RuntimeException("Sheet name is null or empty");
        }

        JsonNode dataRangeSelectedNode = config.get("data_range_selected");
        if (dataRangeSelectedNode == null) {
            throw new RuntimeException("Data range selected is null");
        }
        String dataRangeSelected = dataRangeSelectedNode.asText();
        if (StringUtils.isNullOrEmpty(dataRangeSelected)) {
            throw new RuntimeException("Data range selected is null");
        }

        try (InputStream inputStream = hdfsFileService.streamFile(filePath)) {
            ExcelFileFormat fileFormat = ExcelFileUtils.getFileFormat(filePath);
            try (Workbook workbook = ExcelFileUtils.createWorkbook(inputStream, fileFormat)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new RuntimeException("Sheet not found");
                }

                int[] dataRange = ExcelFileUtils.processFileSelection(sheet, dataRangeSelected);
                int rowsMin = dataRange[0];
                int colsMin = dataRange[1];
                int rowsMax = dataRange[2];
                int colsMax = dataRange[3];

                // Get header
                List<String> header = new LinkedList<>();
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext() && header.isEmpty()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() < rowsMin || row.getRowNum() > rowsMax) {
                        continue;
                    }
                    for (int i = colsMin; i < colsMax; i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null) {
                            header.add(null);
                            continue;
                        }
                        header.add(ExcelFileUtils.getCellValueAsString(cell));
                    }
                }

                // Get data to detect schema
                List<Mapping> schema = new LinkedList<>();
                while (rowIterator.hasNext() && schema.isEmpty()) {
                    Row row = rowIterator.next();
                    int index = 0;
                    for (int i = colsMin; i < colsMax; i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null) {
                            continue;
                        }
                        String cellValue = ExcelFileUtils.getCellValueAsString(cell);
                        if (cellValue.isEmpty()) {
                            schema.add(Mapping.builder()
                                    .fieldName(header.get(index))
                                    .fieldMapping(HeaderNormalizer.normalize(header.get(index)))
                                    .fieldType("text")
                                    .build());
                        } else {
                            schema.add(Mapping.builder()
                                    .fieldName(header.get(index))
                                    .fieldMapping(HeaderNormalizer.normalize(header.get(index)))
                                    .fieldType(TidbDataTypeDetector.detectGeneralizedDataType(cellValue).name())
                                    .build());
                        }
                        index++;
                    }
                }

                return schema;
            }
        } catch (IOException e) {
            log.error("Get file on server error");
            e.printStackTrace();
            throw new RuntimeException("Get file on server error");
        }
    }

    @Override
    public List<String> getSchema(String userId, String fileName, String sheetName, String dataRangeSelected) {
        return ExcelFileUtils.getHeader(hdfsFileService.streamFile(fileName), ExcelFileUtils.getFileFormat(fileName),
                sheetName, dataRangeSelected);
    }

    @Override
    public List<String> getSheets(Long userId, String filePath) {
        return ExcelFileUtils.getSheets(hdfsFileService.streamFile(filePath), ExcelFileUtils.getFileFormat(filePath));
    }

}
