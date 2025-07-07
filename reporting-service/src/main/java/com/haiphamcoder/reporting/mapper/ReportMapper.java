package com.haiphamcoder.reporting.mapper;

import java.util.LinkedList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.dto.ReportDto.ReportConfig;
import com.haiphamcoder.reporting.domain.dto.ReportDto.ReportDtoBuilder;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.Report.ReportBuilder;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ReportMapper {
        public static ReportDto toReportDto(Report report) {
                ReportDtoBuilder builder = ReportDto.builder();
                builder.id(report.getId() != null ? report.getId().toString() : null);
                builder.name(report.getName());
                builder.userId(report.getUserId() != null ? report.getUserId().toString() : null);
                try {
                        builder.config(report.getConfig() != null
                                        ? MapperUtils.objectMapper.readValue(report.getConfig().toString(),
                                                        ReportDto.ReportConfig.class)
                                        : null);
                } catch (JsonProcessingException e) {
                        log.error("Error parsing report config: {}", e.getMessage());
                }
                builder.description(report.getDescription());
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
                if (!StringUtils.isNullOrEmpty(reportDto.getUserId())) {
                        builder.userId(Long.parseLong(reportDto.getUserId()));
                }
                if (reportDto.getConfig() != null) {
                        try {
                                builder.config(MapperUtils.objectMapper.readTree(
                                                MapperUtils.objectMapper.writeValueAsString(reportDto.getConfig())));
                        } catch (JsonProcessingException e) {
                                log.error("Error parsing report config: {}", e.getMessage());
                        }
                } else {
                        try {
                                builder.config(MapperUtils.objectMapper.readTree(
                                                MapperUtils.objectMapper.writeValueAsString(
                                                                ReportConfig.builder().blocks(new LinkedList<>())
                                                                                .build())));
                        } catch (JsonProcessingException e) {
                                log.error("Error parsing report config: {}", e.getMessage());
                        }
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
                if (reportDto.getConfig() != null) {
                        result.setConfig(reportDto.getConfig());
                }
                if (reportDto.getIsDeleted() != null) {
                        result.setIsDeleted(reportDto.getIsDeleted());
                }

                return result;
        }
}
