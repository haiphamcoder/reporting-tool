package com.haiphamcoder.reporting.adapter.dto.mapper;

import java.util.stream.Collectors;

import com.haiphamcoder.reporting.adapter.dto.ReportDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportMapper {
    public static ReportDto toReportDto(Report report) {
        return ReportDto.builder()
                .id(report.getId())
                .name(report.getName())
                .userId(report.getUser().getId().toString())
                .description(report.getDescription())
                .config(report.getConfig())
                .chartIds(report.getCharts().stream().map(Chart::getId).map(String::valueOf)
                        .collect(Collectors.toList()))
                .sharedUserIds(report.getSharedUsers().stream().map(User::getId).map(String::valueOf)
                        .collect(Collectors.toList()))
                .isDeleted(report.getIsDeleted())
                .build();
    }
}
