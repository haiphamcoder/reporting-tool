package com.haiphamcoder.reporting.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.mapper.ChartMapper;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.service.ChartService;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;

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
        try {
            log.info("chart: {}", MapperUtils.objectMapper.writeValueAsString(chart.get()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ChartDto updatedChartDto = ChartMapper.updateChartDto(chart.get(), chartDto);
        Chart updatedChart = chartRepository.save(ChartMapper.toChart(updatedChartDto));
        if (updatedChart == null) {
            throw new RuntimeException("Update chart failed");
        }
        return ChartMapper.toChartDto(updatedChart);
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
        if (StringUtils.isNullOrEmpty(chartDto.getName())) {
            throw new InvalidInputException("name");
        }

        Chart chart = ChartMapper.toChart(chartDto);
        chart.setId(SnowflakeIdGenerator.getInstance().generateId());
        chart.setUserId(userId);

        Chart savedChart = chartRepository.save(chart);
        if (savedChart == null) {
            throw new RuntimeException("Create chart failed");
        }
        return ChartMapper.toChartDto(savedChart);
    }

}
