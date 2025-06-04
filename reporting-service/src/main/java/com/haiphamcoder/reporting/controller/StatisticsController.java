package com.haiphamcoder.reporting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.domain.model.StatisticData;
import com.haiphamcoder.reporting.service.StatisticsService;
import com.haiphamcoder.reporting.shared.http.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping()
    public ResponseEntity<ApiResponse<Object>> getStatistics(@CookieValue(name = "user-id") String userId) {
        StatisticData statisticData = statisticsService.getStatistics(Long.parseLong(userId));
        return ResponseEntity.ok(ApiResponse.success(statisticData, "Get statistics successfully"));
    }

}
