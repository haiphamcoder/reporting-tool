package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.adapter.dto.ChartDto;

public interface ChartService {

    List<ChartDto> getAllChartsByUserId(Long userId);

    ChartDto getChartById(Long userId,Long chartId);

}
