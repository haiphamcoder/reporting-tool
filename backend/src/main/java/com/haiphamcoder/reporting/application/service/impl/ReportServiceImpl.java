package com.haiphamcoder.reporting.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.adapter.dto.ReportDto;
import com.haiphamcoder.reporting.adapter.dto.mapper.ReportMapper;
import com.haiphamcoder.reporting.application.service.ReportService;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    @Override
    public List<ReportDto> getAllReportsByUserId(Long userId) {
        List<Report> reports = reportRepository.getReportsByUserId(userId);
        return reports.stream().map(ReportMapper::toReportDto).collect(Collectors.toList());
    }

}
