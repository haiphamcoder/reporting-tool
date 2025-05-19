package com.haiphamcoder.reporting.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.mapper.ReportMapper;
import com.haiphamcoder.reporting.repository.ReportRepository;
import com.haiphamcoder.reporting.service.ReportService;

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

    @Override
    public ReportDto getReportById(Long userId, Long reportId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReportById'");
    }

    @Override
    public ReportDto updateReport(Long userId, Long reportId, ReportDto reportDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateReport'");
    }

    @Override
    public void deleteReport(Long userId, Long reportId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteReport'");
    }

    @Override
    public ReportDto createReport(Long userId, ReportDto reportDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createReport'");
    }

}
