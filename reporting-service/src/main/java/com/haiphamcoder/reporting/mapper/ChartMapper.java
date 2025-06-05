package com.haiphamcoder.reporting.mapper;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartDtoBuilder;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.Chart.ChartBuilder;
import com.haiphamcoder.reporting.domain.model.QueryOption;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChartMapper {
        public static ChartDto toChartDto(Chart chart) {
                try {
                        ChartDtoBuilder builder = ChartDto.builder();
                        builder.id(chart.getId());
                        builder.name(chart.getName());
                        builder.userId(chart.getUserId());
                        builder.description(chart.getDescription());
                        builder.config(MapperUtils.objectMapper.readValue(chart.getConfig(),
                                        new TypeReference<Map<String, Object>>() {
                                        }));
                        builder.queryOption(
                                        MapperUtils.objectMapper.readValue(chart.getQueryOption(), QueryOption.class));
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
                        builder.id(chartDto.getId());
                        if (!StringUtils.isNullOrEmpty(chartDto.getName())) {
                                builder.name(chartDto.getName());
                        }
                        builder.userId(chartDto.getUserId());
                        if (!StringUtils.isNullOrEmpty(chartDto.getDescription())) {
                                builder.description(chartDto.getDescription());
                        }
                        if (chartDto.getConfig() != null && !chartDto.getConfig().isEmpty()) {
                                builder.config(MapperUtils.objectMapper.writeValueAsString(chartDto.getConfig()));
                        }
                        if (chartDto.getQueryOption() != null) {
                                builder.queryOption(
                                                MapperUtils.objectMapper.writeValueAsString(chartDto.getQueryOption()));
                        }
                        builder.isDeleted(chartDto.getIsDeleted());
                        return builder.build();
                } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                }
        }
}
