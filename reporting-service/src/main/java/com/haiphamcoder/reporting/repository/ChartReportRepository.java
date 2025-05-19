package com.haiphamcoder.reporting.repository;

import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.ChartReport;

public interface ChartReportRepository {
    Optional<ChartReport> getChartReportByChartIdAndReportId(Long chartId, Long reportId);
}
