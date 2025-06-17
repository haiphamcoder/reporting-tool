package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.haiphamcoder.reporting.domain.entity.Report;

public interface ReportRepository {

    Optional<Report> getReportById(Long id);

    Page<Report> getReportsByUserId(Long userId, Integer page, Integer limit);

    Long getTotalReportByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getReportCountByLast30Days(Long userId);

    Optional<Report> updateReport(Report report);

    Optional<Report> createReport(Report report);

}
