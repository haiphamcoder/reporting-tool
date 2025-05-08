package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Report;

public interface ReportRepository {

    Optional<Report> getReportById(Long id);

    List<Report> getReportsByUserId(Long userId);

    Long getTotalReportByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getReportCountByLast30Days(Long userId);
}
