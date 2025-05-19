package com.haiphamcoder.reporting.mapper;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.entity.Chart;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChartMapper {
        public static ChartDto toChartDto(Chart chart) {
                return ChartDto.builder()
                                .id(chart.getId())
                                .name(chart.getName())
                                .userId(chart.getUserId().toString())
                                .description(chart.getDescription())
                                .config(chart.getConfig())
                                .queryOption(chart.getQueryOption())
                                .isDeleted(chart.getIsDeleted())
                                .build();
        }
}
