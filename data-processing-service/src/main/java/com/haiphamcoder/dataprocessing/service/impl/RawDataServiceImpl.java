package com.haiphamcoder.dataprocessing.service.impl;

import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.domain.dto.ChartDto;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.exception.SourceNotFoundException;
import com.haiphamcoder.dataprocessing.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.dataprocessing.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.dataprocessing.domain.model.ChartData;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.service.ChartGrpcClient;
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
    private final ChartGrpcClient chartGrpcClient;
    private final StorageService storageService;

    @Override
    public PreviewData previewSource(Long sourceId, Integer page, Integer limit) {
        SourceDto source = sourceGrpcClient.getSourceById(sourceId);

        if (source == null || source.getMapping() == null) {
            throw new SourceNotFoundException("Source not found");
        }

        List<JSONObject> data = storageService.getPreviewData(source, page, limit);

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
    public ChartData getChartData(Long chartId, Integer page, Integer limit) {
        ChartDto chart = chartGrpcClient.getChartById(chartId);
        if (chart == null) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (chart.getQueryOption() == null) {
            throw new InvalidInputException("Query option is required");
        }

        ChartData chartData = new ChartData();
        // TODO: Implement chart data fetching
        return chartData;
    }
}
