package com.haiphamcoder.dataprocessing.service;

import com.haiphamcoder.dataprocessing.domain.dto.ChartDto;

public interface ChartGrpcClient {

    public ChartDto getChartById(Long id);

    public ChartDto updateChart(ChartDto chart);
    
}
