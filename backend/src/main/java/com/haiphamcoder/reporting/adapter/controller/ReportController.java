package com.haiphamcoder.reporting.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.adapter.dto.ReportDto;
import com.haiphamcoder.reporting.application.service.ReportService;
import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "report", description = "Report controller")
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

}
