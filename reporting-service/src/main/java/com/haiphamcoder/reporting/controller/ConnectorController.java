package com.haiphamcoder.reporting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.domain.entity.Connector;
import com.haiphamcoder.reporting.service.ConnectorService;
import com.haiphamcoder.reporting.shared.http.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/connectors")
@RequiredArgsConstructor
public class ConnectorController {
    private final ConnectorService connectorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllEnabledConnectors() {
        List<Connector> connectors = connectorService.getAllEnabledConnectors();
        return ResponseEntity.ok(ApiResponse.success(connectors, "Get all enabled connectors successfully"));
    }
}
