package com.haiphamcoder.reporting.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartDtoBuilder;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.Chart.ChartBuilder;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChartMapper {
        public static ChartDto toChartDto(Chart chart) {
                try {
                        ChartDtoBuilder builder = ChartDto.builder();
                        builder.id(chart.getId() != null ? chart.getId().toString() : null);
                        builder.name(chart.getName());
                        builder.userId(chart.getUserId() != null ? chart.getUserId().toString() : null);
                        builder.description(chart.getDescription());

                        builder.config(chart.getConfig() != null
                                        ? MapperUtils.objectMapper.readValue(chart.getConfig().toString(),
                                                        ChartDto.ChartConfig.class)
                                        : null);
                        builder.sqlQuery(chart.getSqlQuery());
                        builder.isDeleted(chart.getIsDeleted());
                        builder.createdAt(chart.getCreatedAt());
                        builder.modifiedAt(chart.getModifiedAt());
                        return builder.build();
                } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                }
        }

        public static Chart toChart(ChartDto chartDto) {
                try {
                        ChartBuilder builder = Chart.builder();
                        builder.id(chartDto.getId() != null ? Long.parseLong(chartDto.getId()) : null);
                        if (!StringUtils.isNullOrEmpty(chartDto.getName())) {
                                builder.name(chartDto.getName());
                        }
                        builder.userId(chartDto.getUserId() != null ? Long.parseLong(chartDto.getUserId()) : null);
                        if (!StringUtils.isNullOrEmpty(chartDto.getDescription())) {
                                builder.description(chartDto.getDescription());
                        }
                        if (chartDto.getConfig() != null) {
                                builder.config(MapperUtils.objectMapper.readTree(
                                                MapperUtils.objectMapper.writeValueAsString(chartDto.getConfig())));
                        }
                        if (!StringUtils.isNullOrEmpty(chartDto.getSqlQuery())) {
                                builder.sqlQuery(chartDto.getSqlQuery());
                        }
                        if (chartDto.getIsDeleted() != null) {
                                builder.isDeleted(chartDto.getIsDeleted());
                        }
                        if (chartDto.getCreatedAt() != null) {
                                builder.createdAt(chartDto.getCreatedAt());
                        }
                        if (chartDto.getModifiedAt() != null) {
                                builder.modifiedAt(chartDto.getModifiedAt());
                        }
                        return builder.build();
                } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                }
        }

        public static ChartDto updateChartDto(Chart chart, ChartDto chartDto) {
                ChartDto result = toChartDto(chart);
                if (chartDto.getId() != null) {
                        result.setId(chartDto.getId());
                }
                if (!StringUtils.isNullOrEmpty(chartDto.getName())) {
                        result.setName(chartDto.getName());
                }
                if (!StringUtils.isNullOrEmpty(chartDto.getDescription())) {
                        result.setDescription(chartDto.getDescription());
                }
                if (chartDto.getConfig() != null) {
                        result.setConfig(chartDto.getConfig());
                }
                if (!StringUtils.isNullOrEmpty(chartDto.getSqlQuery())) {
                        result.setSqlQuery(chartDto.getSqlQuery());
                }
                if (chartDto.getIsDeleted() != null) {
                        result.setIsDeleted(chartDto.getIsDeleted());
                }
                if (chartDto.getUserId() != null) {
                        result.setUserId(chartDto.getUserId());
                }
                return result;
        }
}
