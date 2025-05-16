package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.adapter.dto.ReportDto;

public interface ReportService {

    List<ReportDto> getAllReportsByUserId(Long userId);

}
