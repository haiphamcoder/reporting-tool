package com.haiphamcoder.reporting.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.mapper.ChartMapper;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.service.ChartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
    private final ChartRepository chartRepository;

    @Override
    public List<ChartDto> getAllChartsByUserId(Long userId) {
        List<Chart> charts = chartRepository.getAllChartsByUserId(userId);
        return charts.stream().map(ChartMapper::toChartDto).collect(Collectors.toList());
    }

    @Override
    public ChartDto getChartById(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        return ChartMapper.toChartDto(chart.get());
    }

    @Override
    public ChartDto updateChart(Long userId, Long chartId, ChartDto chartDto) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        chart.get().setDescription(chartDto.getDescription());
        chart.get().setConfig(chartDto.getConfig());
        chart.get().setQueryOption(chartDto.getQueryOption());
        chart.get().setIsDeleted(chartDto.getIsDeleted());
        chartRepository.updateChart(chart.get());
        return ChartMapper.toChartDto(chart.get());
    }

    @Override
    public void deleteChart(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        chart.get().setIsDeleted(true);
        chartRepository.updateChart(chart.get());
    }

    @Override
    public ChartDto createChart(Long userId, ChartDto chartDto) {
        chartDto.setUserId(userId.toString());
        chartDto.setIsDeleted(false);
        chartRepository.updateChart(ChartMapper.toChart(chartDto));
        return chartDto;
    }

}
