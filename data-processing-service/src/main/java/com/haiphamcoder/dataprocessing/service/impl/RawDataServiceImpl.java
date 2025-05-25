package com.haiphamcoder.dataprocessing.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.exception.SourceNotFoundException;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.service.RawDataService;
import com.haiphamcoder.dataprocessing.service.SourceGrpcClient;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawDataServiceImpl implements RawDataService {

    private final SourceGrpcClient sourceGrpcClient;
    private final StorageService storageService;

    @Override
    public PreviewData previewSource(Long sourceId) {
        SourceDto source = sourceGrpcClient.getSourceById(sourceId);

        if (source == null || source.getMapping() == null) {
            throw new SourceNotFoundException("Source not found");
        }

        List<JSONObject> data = storageService.getPreviewData(source, 10);

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
}
