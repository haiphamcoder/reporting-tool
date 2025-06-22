package com.haiphamcoder.reporting.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.model.request.CreateChartRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.shared.Pair;

public interface ChartService {

    Pair<List<ChartDto>, Metadata> getAllChartsByUserId(Long userId, Integer page, Integer limit);

    ChartDto getChartById(Long userId,Long chartId);

    ChartDto updateChart(Long userId, Long chartId, ChartDto chartDto);

    void deleteChart(Long userId, Long chartId);

    ChartDto createChart(Long userId, CreateChartRequest request);

}
