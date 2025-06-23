package com.haiphamcoder.reporting.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.model.QueryOption;
import com.haiphamcoder.reporting.domain.model.QueryOption.Join;
import com.haiphamcoder.reporting.domain.model.request.CreateChartRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.ChartMapper;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.SourceRepository;
import com.haiphamcoder.reporting.service.ChartService;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.QueryOptionToSqlConverter;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
    private final ChartRepository chartRepository;
    private final SourceRepository sourceRepository;

    @Override
    public Pair<List<ChartDto>, Metadata> getAllChartsByUserId(Long userId, Integer page, Integer limit) {
        Page<Chart> charts = chartRepository.getAllChartsByUserId(userId, page, limit);
        return new Pair<>(charts.stream().map(ChartMapper::toChartDto).collect(Collectors.toList()),
                Metadata.builder()
                        .totalElements(charts.getTotalElements())
                        .numberOfElements(charts.getNumberOfElements())
                        .totalPages(charts.getTotalPages())
                        .currentPage(charts.getNumber())
                        .pageSize(charts.getSize())
                        .build());
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
    public ChartDto createChart(Long userId, CreateChartRequest request) {
        if (StringUtils.isNullOrEmpty(request.getName())) {
            throw new InvalidInputException("name");
        }

        ChartDto chartDto = ChartDto.builder()
                .config(request.getConfig())
                .name(request.getName())
                .description(request.getDescription())
                .sqlQuery(request.getSqlQuery())
                .build();
        Chart chart = ChartMapper.toChart(chartDto);
        chart.setId(SnowflakeIdGenerator.getInstance().generateId());
        chart.setUserId(userId);

        Chart savedChart = chartRepository.save(chart);
        if (savedChart == null) {
            throw new RuntimeException("Create chart failed");
        }
        return ChartMapper.toChartDto(savedChart);
    }

    @Override
    public String convertQueryToSql(Long userId, QueryOption queryOption) {
        Optional<Source> source = sourceRepository.getSourceById(Long.parseLong(queryOption.getTable()));
        if (source.isEmpty()) {
            throw new ResourceNotFoundException("Source", queryOption.getTable());
        }
        long sourceUserId = source.get().getUserId();
        if (sourceUserId != userId) {
            throw new ForbiddenException("You are not allowed to access this source");
        }
        Map<String, String> sourceTableNames = new HashMap<>();
        sourceTableNames.put(source.get().getId().toString(), source.get().getTableName());

        List<Join> joins = queryOption.getJoins();
        if (joins != null && !joins.isEmpty()) {
            for (Join join : joins) {
                Optional<Source> joinSource = sourceRepository.getSourceById(Long.parseLong(join.getTable()));
                if (joinSource.isEmpty()) {
                    throw new ResourceNotFoundException("Source", join.getTable());
                }
                long joinSourceUserId = joinSource.get().getUserId();
                if (joinSourceUserId != userId) {
                    throw new ForbiddenException("You are not allowed to access this source");
                }
                sourceTableNames.put(joinSource.get().getId().toString(), joinSource.get().getTableName());
            }
        }
        return QueryOptionToSqlConverter.convertToSql(queryOption, source.get().getTableName(), sourceTableNames);
    }
}
