package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.ChartReport;

public interface ChartReportRepository {

    Optional<ChartReport> getChartReportByChartIdAndReportId(Long chartId, Long reportId);

    List<ChartReport> getChartReportsByReportId(Long reportId);

    void save(ChartReport chartReport);

    void deleteByChartIdAndReportId(Long chartId, Long reportId);

}
