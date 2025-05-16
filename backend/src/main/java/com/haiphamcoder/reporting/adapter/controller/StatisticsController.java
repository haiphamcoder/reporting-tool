package com.haiphamcoder.reporting.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.application.service.StatisticsService;
import com.haiphamcoder.reporting.domain.model.StatisticData;
import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "statistics", description = "Statistics controller")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping()
    public ResponseEntity<Object> getStatistics(@CookieValue(name = "user-id") String userId) {
        try {
            StatisticData statisticData = statisticsService.getStatistics(Long.parseLong(userId));
            return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(statisticData));
        } catch (BaseException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

}
