package com.haiphamcoder.reporting.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.adapter.dto.ChartDto;
import com.haiphamcoder.reporting.application.service.ChartService;
import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chart")
@Tag(name = "chart", description = "Chart controller")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @GetMapping
    public ResponseEntity<Object> getAll(@CookieValue(name = "user-id") String userId) {
        try {
            List<ChartDto> charts = chartService.getAllChartsByUserId(Long.parseLong(userId));
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(charts));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @GetMapping("/{chart-id}")
    public ResponseEntity<Object> getById(@CookieValue(name = "user-id") String userId,
            @PathVariable("chart-id") String chartId) {
        try {
            ChartDto chart = null;
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(chart));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

}
