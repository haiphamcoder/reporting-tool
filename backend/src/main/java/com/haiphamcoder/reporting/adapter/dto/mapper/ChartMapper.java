package com.haiphamcoder.reporting.adapter.dto.mapper;

import java.util.stream.Collectors;

import com.haiphamcoder.reporting.adapter.dto.ChartDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChartMapper {
    public static ChartDto toChartDto(Chart chart) {
        return ChartDto.builder()
                .id(chart.getId())
                .name(chart.getName())
                .userId(chart.getUser().getId().toString())
                .description(chart.getDescription())
                .config(chart.getConfig())
                .queryOption(chart.getQueryOption())
                .reportIds(chart.getReports().stream().map(Report::getId).map(String::valueOf)
                        .collect(Collectors.toList()))
                .sharedUserIds(chart.getSharedUsers().stream().map(User::getId).map(String::valueOf)
                        .collect(Collectors.toList()))
                .isDeleted(chart.getIsDeleted())
                .build();
    }
}
