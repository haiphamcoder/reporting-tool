package com.haiphamcoder.dataprocessing.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.exception.SourceNotFoundException;
import com.haiphamcoder.dataprocessing.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.dataprocessing.domain.model.GetChartPreviewDataRequest;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.domain.model.request.UpdateSourceDataRequest;
import com.haiphamcoder.dataprocessing.service.RawDataService;
import com.haiphamcoder.dataprocessing.service.SourceGrpcClient;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;
import com.haiphamcoder.dataprocessing.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawDataServiceImpl implements RawDataService {

    private final SourceGrpcClient sourceGrpcClient;
    private final StorageService storageService;

    @Override
    public PreviewData previewSource(Long sourceId, String search, String searchBy, Integer page, Integer limit) {
        SourceDto source = sourceGrpcClient.getSourceById(sourceId);

        if (source == null || source.getMapping() == null) {
            throw new SourceNotFoundException("Source not found");
        }

        List<JSONObject> data = storageService.getPreviewData(source, search, searchBy, page, limit);

        PreviewData previewData = new PreviewData();
        previewData.setSchema(source.getMapping());
        previewData.setRecords(new LinkedList<>());
        for (JSONObject record : data) {
            try {
                previewData.getRecords().add(MapperUtils.objectMapper.readTree(record.toString()));
            } catch (Exception e) {
                log.error("Error parsing record: {}", record.toString());
                continue;
            }
        }
        return previewData;
    }

    @Override
    public void updateSourceData(Long sourceId, UpdateSourceDataRequest request) {
        SourceDto source = sourceGrpcClient.getSourceById(sourceId);
        if (source == null || source.getMapping() == null) {
            throw new SourceNotFoundException("Source not found");
        }

        Map<String, Object> data = request.getData();
        if (data == null) {
            throw new InvalidInputException("Data is required");
        }

        storageService.updateSourceData(source, data);
    }

    @Override
    public PreviewData getChartPreviewData(GetChartPreviewDataRequest request, Integer page, Integer limit) {
        if (request.getSqlQuery() == null) {
            throw new InvalidInputException("SQL query is required");
        }

        String sqlQuery = request.getSqlQuery();
        if (!sqlQuery.contains("limit") && !sqlQuery.contains("offset")) {
            sqlQuery = sqlQuery + " limit " + limit + " offset " + page * limit;
        }

        List<JSONObject> data = storageService.getPreviewDataByQuery(sqlQuery);
        PreviewData previewData = new PreviewData();
        previewData.setSchema(request.getFields().stream().map(field -> Mapping.builder()
                .fieldName(StringUtils.isNullOrEmpty(field.getAlias()) ? field.getFieldName() : field.getAlias())
                .fieldType(field.getDataType()).build()).toList());
        previewData.setRecords(new LinkedList<>());
        for (JSONObject record : data) {
            try {
                previewData.getRecords().add(MapperUtils.objectMapper.readTree(record.toString()));
            } catch (Exception e) {
                log.error("Error parsing record: {}", record.toString());
            }
        }
        return previewData;
    }
}
