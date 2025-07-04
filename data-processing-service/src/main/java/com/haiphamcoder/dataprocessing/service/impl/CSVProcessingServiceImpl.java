package com.haiphamcoder.dataprocessing.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.CSVProcessingService;
import com.haiphamcoder.dataprocessing.service.HdfsFileService;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;
import com.haiphamcoder.dataprocessing.shared.StringUtils;
import com.haiphamcoder.dataprocessing.shared.TidbDataTypeDetector;
import com.haiphamcoder.dataprocessing.shared.processing.CSVFileUtils;
import com.haiphamcoder.dataprocessing.shared.processing.HeaderNormalizer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVProcessingServiceImpl implements CSVProcessingService {
    private final HdfsFileService hdfsFileService;

    @Override
    public List<String> getSchema(String userId, String fileName) {
        return CSVFileUtils.getFieldNames(hdfsFileService.streamFile(fileName));
    }

    @Override
    public PreviewData getPreviewData(String userId, String filePath, Integer limit) {
        PreviewData previewData = new PreviewData();

        char delimiter = ',';
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(hdfsFileService.streamFile(filePath)))) {
            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                throw new RuntimeException("File error");
            }
            delimiter = CSVFileUtils.detectDelimiter(firstLine);
        } catch (IOException e) {
            log.error("Get file on server error");
            e.printStackTrace();
            throw new RuntimeException("Get file on server error");
        }

        try (CSVReader csvReader = CSVFileUtils.createCSVReader(hdfsFileService.streamFile(filePath),
                delimiter)) {
            String[] fieldNameArray = csvReader.readNext();
            List<String> fieldNames = new LinkedList<>();
            for (String fieldName : fieldNameArray) {
                fieldNames.add(HeaderNormalizer.normalize(fieldName));
            }

            if (fieldNames.isEmpty()) {
                throw new Exception("File error");
            }

            previewData.setSchema(fieldNames.stream().map(fieldName -> Mapping.builder()
                    .fieldName(fieldName)
                    .fieldMapping(fieldName)
                    .fieldType("text")
                    .build())
                    .collect(Collectors.toList()));

            previewData.setRecords(new LinkedList<>());
            int recordCount = 0;
            String[] record;
            while (recordCount < limit && (record = csvReader.readNext()) != null) {
                JSONObject recordJson = new JSONObject();
                for (int i = 0; i < fieldNames.size(); i++) {
                    recordJson.put(fieldNames.get(i), record[i]);
                }
                if (recordJson.length() > 0) {
                    previewData.getRecords().add(MapperUtils.objectMapper.readTree(recordJson.toString()));
                }
                recordCount++;
            }
        } catch (Exception e) {
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

        log.info("Config: {}", config);

        String filePath = config.get("file_path").asText();
        if (StringUtils.isNullOrEmpty(filePath)) {
            throw new RuntimeException("File path is null");
        }

        char delimiter = ',';
        try (CSVReader csvReader = CSVFileUtils.createCSVReader(hdfsFileService.streamFile(filePath), delimiter)) {
            String[] fieldNameArray = csvReader.readNext();
            List<String> fieldNames = new LinkedList<>();
            for (String fieldName : fieldNameArray) {
                fieldNames.add(fieldName);
            }

            List<Mapping> mappings = new LinkedList<>();
            String[] firstRecord = csvReader.readNext();
            if (firstRecord == null || (firstRecord != null && firstRecord.length != fieldNames.size())) {
                mappings = fieldNames.stream().map(fieldName -> Mapping.builder()
                        .fieldName(fieldName)
                        .fieldMapping(HeaderNormalizer.normalize(fieldName))
                        .fieldType("text")
                        .build())
                        .collect(Collectors.toList());
            } else {
                for (int i = 0; i < fieldNames.size(); i++) {
                    mappings.add(Mapping.builder()
                            .fieldName(fieldNames.get(i))
                            .fieldMapping(HeaderNormalizer.normalize(fieldNames.get(i)))
                            .fieldType(TidbDataTypeDetector.detectGeneralizedDataType(firstRecord[i]).name())
                            .build());
                }
            }

            return mappings;
        } catch (IOException e) {
            log.error("Get file on server error");
            e.printStackTrace();
            throw new RuntimeException("Get file on server error");
        } catch (CsvValidationException e) {
            log.error("Get file on server error");
        }

        return new LinkedList<>();
    }

}
