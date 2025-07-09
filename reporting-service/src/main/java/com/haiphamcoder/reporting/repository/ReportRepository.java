package com.haiphamcoder.reporting.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.haiphamcoder.reporting.domain.entity.Report;

public interface ReportRepository {

    Optional<Report> getReportById(Long id);

    Page<Report> getReportsByUserId(Long userId, Integer page, Integer limit);
    
    Page<Report> getReportsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page, Integer limit);
    
    default Page<Report> getReportsByUserId(Long userId, String search, Integer page, Integer limit) {
        return getReportsByUserIdAndIsDeleted(userId, false, search, page, limit);
    }

    Page<Report> getReportsByUserIdOrReportId(Long userId, Set<Long> reportIds, String search, Integer page, Integer limit);

    Long getTotalReportByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Long getTotalReportByUserIdOrReportIdAndIsDeleted(Long userId, Set<Long> reportIds, Boolean isDeleted);

    Long getReportCountByUserIdOrReportIdAndIsDeletedAndCreatedDate(Long userId, Set<Long> reportIds, Boolean isDeleted,
            LocalDate date);

    Optional<Report> updateReport(Report report);

    Optional<Report> createReport(Report report);

    Report save(Report report);

}
