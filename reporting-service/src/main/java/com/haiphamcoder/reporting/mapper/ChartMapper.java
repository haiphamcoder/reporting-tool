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

        public static Chart toChart(ChartDto chartDto) {
                return Chart.builder()
                                .id(chartDto.getId())
                                .name(chartDto.getName())
                                .userId(Long.parseLong(chartDto.getUserId()))
                                .description(chartDto.getDescription())
                                .config(chartDto.getConfig())
                                .queryOption(chartDto.getQueryOption())
                                .isDeleted(chartDto.getIsDeleted())
                                .build();
        }
}
