package com.haiphamcoder.reporting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.domain.entity.Connector;
import com.haiphamcoder.reporting.service.ConnectorService;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/connectors")
@RequiredArgsConstructor
public class ConnectorController {
    private final ConnectorService connectorService;

    @GetMapping
    public ResponseEntity<RestAPIResponse<List<Connector>>> getAllEnabledConnectors() {
        List<Connector> connectors = connectorService.getAllEnabledConnectors();
        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(connectors));
    }
}
