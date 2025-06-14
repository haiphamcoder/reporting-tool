package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.adapter.dto.ChartDto;

public interface ChartService {

    List<ChartDto> getAllChartsByUserId(Long userId);

    ChartDto getChartById(Long userId,Long chartId);

    ChartDto updateChart(Long userId, Long chartId, ChartDto chartDto);

    void deleteChart(Long userId, Long chartId);

    ChartDto createChart(Long userId, ChartDto chartDto);

}
