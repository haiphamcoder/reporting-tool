package com.haiphamcoder.reporting.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.dto.ReportDto;

public interface ReportService {

    List<ReportDto> getAllReportsByUserId(Long userId);

    ReportDto getReportById(Long userId, Long reportId);

    ReportDto updateReport(Long userId, Long reportId, ReportDto reportDto);

    void deleteReport(Long userId, Long reportId);

    ReportDto createReport(Long userId, ReportDto reportDto);

    void addChartToReport(Long userId, Long reportId, Long chartId);

}
