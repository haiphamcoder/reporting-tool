package com.haiphamcoder.reporting.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.adapter.dto.ChartDto;
import com.haiphamcoder.reporting.adapter.dto.mapper.ChartMapper;
import com.haiphamcoder.reporting.application.service.ChartService;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.repository.ChartRepository;

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

}
