package com.haiphamcoder.cdp.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.shared.MapperUtils;
import com.haiphamcoder.cdp.shared.processing.CSVFileUtils;
import com.haiphamcoder.cdp.shared.processing.HeaderNormalizer;
import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVProcessingService implements HdfsFileProcessingService {
    private final HdfsFileService hdfsFileService;

    @Override
    public List<String> getSchema(String userId, String fileName) {
        return CSVFileUtils.getFieldNames(hdfsFileService.streamFile(userId, fileName));
    }

    public PreviewData getPreviewData(String userId, String filePath, Integer limit) {
        PreviewData previewData = new PreviewData();

        char delimiter = ',';
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(hdfsFileService.streamFile(userId, filePath)))) {
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

        try (CSVReader csvReader = CSVFileUtils.createCSVReader(hdfsFileService.streamFile(userId, filePath),
                delimiter)) {
            String[] fieldNameArray = csvReader.readNext();
            List<String> fieldNames = new LinkedList<>();
            for (String fieldName : fieldNameArray) {
                fieldNames.add(HeaderNormalizer.normalize(fieldName));
            }

            if (fieldNames.isEmpty()) {
                throw new Exception("File error");
            }

            previewData.setSchema(fieldNames);

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

}
