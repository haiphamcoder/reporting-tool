package com.haiphamcoder.reporting.mapper;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.entity.Report;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportMapper {
        public static ReportDto toReportDto(Report report) {
                return ReportDto.builder()
                                .id(report.getId())
                                .name(report.getName())
                                .userId(report.getUserId().toString())
                                .description(report.getDescription())
                                .config(report.getConfig())
                                .isDeleted(report.getIsDeleted())
                                .build();
        }
}
