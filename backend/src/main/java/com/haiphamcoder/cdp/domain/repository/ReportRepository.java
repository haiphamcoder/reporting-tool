package com.haiphamcoder.cdp.domain.repository;

import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Report;

public interface ReportRepository {

    Optional<Report> getReportById(Long id);
    
}
