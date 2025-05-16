package com.haiphamcoder.reporting.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.application.service.ConnectorService;
import com.haiphamcoder.reporting.domain.entity.Connector;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/connector")
@Tag(name = "connectors", description = "Connector controller")
@RequiredArgsConstructor
public class ConnectorController {
    private final ConnectorService connectorService;

    @GetMapping
    public ResponseEntity<RestAPIResponse<List<Connector>>> getAllEnabledConnectors() {
        List<Connector> connectors = connectorService.getAllEnabledConnectors();
        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(connectors));
    }
}
