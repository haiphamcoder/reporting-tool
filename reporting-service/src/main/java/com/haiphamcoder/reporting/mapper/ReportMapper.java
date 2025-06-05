package com.haiphamcoder.reporting.mapper;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.Report.ReportBuilder;

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

        public static Report toEntity(ReportDto reportDto) {
                ReportBuilder builder = Report.builder();
                if (reportDto.getId() != null) {
                        builder.id(reportDto.getId());
                }
                if (reportDto.getName() != null) {
                        builder.name(reportDto.getName());
                }
                return builder.build();
        }
}
