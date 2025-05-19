package com.haiphamcoder.reporting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.service.ReportService;
import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<Object> getAll(@CookieValue(name = "user-id") String userId) {
        try {
            List<ReportDto> reports = reportService.getAllReportsByUserId(Long.parseLong(userId));
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(reports));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @GetMapping("/{report-id}")
    public ResponseEntity<Object> getById(@CookieValue(name = "user-id") String userId,
            @PathVariable("report-id") String reportId) {
        try {
            ReportDto report = reportService.getReportById(Long.parseLong(userId), Long.parseLong(reportId));
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(report));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @PutMapping("/{report-id}")
    public ResponseEntity<Object> update(@CookieValue(name = "user-id") String userId,
            @PathVariable("report-id") String reportId,
            @RequestBody ReportDto reportDto) {
        try {
            ReportDto updatedReport = reportService.updateReport(Long.parseLong(userId), Long.parseLong(reportId), reportDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(updatedReport));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @DeleteMapping("/{report-id}")
    public ResponseEntity<Object> delete(@CookieValue(name = "user-id") String userId,
            @PathVariable("report-id") String reportId) {
        try {
            reportService.deleteReport(Long.parseLong(userId), Long.parseLong(reportId));
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Report deleted successfully"));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @PostMapping
    public ResponseEntity<Object> create(@CookieValue(name = "user-id") String userId,
            @RequestBody ReportDto reportDto) {
        try {
            ReportDto createdReport = reportService.createReport(Long.parseLong(userId), reportDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(createdReport));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

}
