package com.haiphamcoder.dataprocessing.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.service.ChartDataService;
import com.haiphamcoder.dataprocessing.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartDataServiceImpl implements ChartDataService {
    private final StorageService storageService;

    @Override
    public Map<String, Object> getChartData(Long chartId) {
        log.info("Getting data for chart {}", chartId);
        try {
            // Tạo SourceDto để truy vấn dữ liệu
            SourceDto sourceDto = SourceDto.builder()
                    .id(chartId)
                    .name("chart_" + chartId)
                    .build();

            // Lấy dữ liệu mới nhất từ StorageService
            List<JSONObject> data = storageService.getPreviewData(sourceDto, 1);
            if (!data.isEmpty()) {
                return data.get(0).toMap();
            }
            return new HashMap<>();
        } catch (Exception e) {
            log.error("Error getting data for chart {}: {}", chartId, e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getChartsData(List<Long> chartIds) {
        log.info("Getting data for charts: {}", chartIds);
        List<Map<String, Object>> chartsData = new ArrayList<>();
        for (Long chartId : chartIds) {
            Map<String, Object> chartData = getChartData(chartId);
            if (!chartData.isEmpty()) {
                chartsData.add(chartData);
            }
        }
        return chartsData;
    }

    @Override
    public boolean hasChartData(Long chartId) {
        log.info("Checking data existence for chart {}", chartId);
        try {
            // Tạo SourceDto để kiểm tra dữ liệu
            SourceDto sourceDto = SourceDto.builder()
                    .id(chartId)
                    .name("chart_" + chartId)
                    .build();

            // Lấy một bản ghi để kiểm tra sự tồn tại
            List<JSONObject> data = storageService.getPreviewData(sourceDto, 1);
            return !data.isEmpty();
        } catch (Exception e) {
            log.error("Error checking data existence for chart {}: {}", chartId, e.getMessage());
            return false;
        }
    }

    @Override
    public String getLastUpdateTime(Long chartId) {
        log.info("Getting last update time for chart {}", chartId);
        try {
            // Tạo SourceDto để lấy dữ liệu
            SourceDto sourceDto = SourceDto.builder()
                    .id(chartId)
                    .name("chart_" + chartId)
                    .build();

            // Lấy bản ghi mới nhất để lấy thời gian cập nhật
            List<JSONObject> data = storageService.getPreviewData(sourceDto, 1);
            if (!data.isEmpty()) {
                return data.get(0).optString("updated_at");
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting last update time for chart {}: {}", chartId, e.getMessage());
            return null;
        }
    }
}
