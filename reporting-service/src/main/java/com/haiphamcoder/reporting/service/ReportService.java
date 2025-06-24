package com.haiphamcoder.reporting.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.model.request.CreateReportRequest;
import com.haiphamcoder.reporting.domain.model.request.UpdateReportRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.shared.Pair;

public interface ReportService {

    Pair<List<ReportDto>, Metadata> getAllReportsByUserId(Long userId, Integer page, Integer limit);

    ReportDto getReportById(Long userId, Long reportId);

    ReportDto updateReport(Long userId, Long reportId, UpdateReportRequest updateReportRequest);

    void deleteReport(Long userId, Long reportId);

    ReportDto createReport(Long userId, CreateReportRequest createReportRequest);

    void addChartToReport(Long userId, Long reportId, Long chartId);

}
