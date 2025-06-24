package com.haiphamcoder.reporting.mapper;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.dto.ReportDto.ReportDtoBuilder;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.Report.ReportBuilder;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportMapper {
        public static ReportDto toReportDto(Report report) {
                ReportDtoBuilder builder = ReportDto.builder();
                builder.id(report.getId() != null ? report.getId().toString() : null);
                builder.name(report.getName());
                builder.userId(report.getUserId() != null ? report.getUserId().toString() : null);
                builder.description(report.getDescription());
                builder.chartIds(report.getChartIds());
                builder.isDeleted(report.getIsDeleted());
                builder.createdAt(report.getCreatedAt());
                builder.modifiedAt(report.getModifiedAt());
                return builder.build();
        }

        public static Report toEntity(ReportDto reportDto) {
                ReportBuilder builder = Report.builder();
                if (!StringUtils.isNullOrEmpty(reportDto.getId())) {
                        builder.id(Long.parseLong(reportDto.getId()));
                }
                if (reportDto.getName() != null) {
                        builder.name(reportDto.getName());
                }
                if (reportDto.getDescription() != null) {
                        builder.description(reportDto.getDescription());
                }
                if (reportDto.getChartIds() != null) {
                        builder.chartIds(reportDto.getChartIds());
                }
                if (reportDto.getIsDeleted() != null) {
                        builder.isDeleted(reportDto.getIsDeleted());
                }
                if (reportDto.getCreatedAt() != null) {
                        builder.createdAt(reportDto.getCreatedAt());
                }
                if (reportDto.getModifiedAt() != null) {
                        builder.modifiedAt(reportDto.getModifiedAt());
                }
                return builder.build();
        }

        public static ReportDto updateReportDto(Report report, ReportDto reportDto) {
                ReportDto result = toReportDto(report);

                if (reportDto.getId() != null) {
                        result.setId(reportDto.getId());
                }
                if (reportDto.getName() != null) {
                        result.setName(reportDto.getName());
                }
                if (reportDto.getDescription() != null) {
                        result.setDescription(reportDto.getDescription());
                }
                if (reportDto.getChartIds() != null) {
                        result.setChartIds(reportDto.getChartIds());
                }
                if (reportDto.getIsDeleted() != null) {
                        result.setIsDeleted(reportDto.getIsDeleted());
                }

                return result;
        }
}
