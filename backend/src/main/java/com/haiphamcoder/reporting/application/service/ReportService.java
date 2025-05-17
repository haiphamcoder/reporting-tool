package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.adapter.dto.ReportDto;

public interface ReportService {

    List<ReportDto> getAllReportsByUserId(Long userId);

    ReportDto getReportById(Long userId, Long reportId);

    ReportDto updateReport(Long userId, Long reportId, ReportDto reportDto);

    void deleteReport(Long userId, Long reportId);

    ReportDto createReport(Long userId, ReportDto reportDto);

}
