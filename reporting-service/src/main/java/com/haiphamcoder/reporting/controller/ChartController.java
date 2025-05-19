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

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.service.ChartService;
import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/charts")
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

    @PostMapping
    public ResponseEntity<Object> create(@CookieValue(name = "user-id") String userId,
            @RequestBody ChartDto chartDto) {
        try {
            ChartDto createdChart = chartService.createChart(Long.parseLong(userId), chartDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(createdChart));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @PutMapping("/{chart-id}")
    public ResponseEntity<Object> update(@CookieValue(name = "user-id") String userId,
            @PathVariable("chart-id") String chartId,
            @RequestBody ChartDto chartDto) {
        try {
            ChartDto updatedChart = chartService.updateChart(Long.parseLong(userId), Long.parseLong(chartId), chartDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(updatedChart));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

    @DeleteMapping("/{chart-id}")
    public ResponseEntity<Object> delete(@CookieValue(name = "user-id") String userId,
            @PathVariable("chart-id") String chartId) {
        try {
            chartService.deleteChart(Long.parseLong(userId), Long.parseLong(chartId));
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Chart deleted successfully"));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(RestAPIResponse.ResponseFactory.createResponse(e));
        }
    }

}
