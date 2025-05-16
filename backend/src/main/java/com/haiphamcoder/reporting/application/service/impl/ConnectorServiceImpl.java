package com.haiphamcoder.reporting.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.application.service.ConnectorService;
import com.haiphamcoder.reporting.domain.entity.Connector;
import com.haiphamcoder.reporting.domain.repository.ConnectorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectorServiceImpl implements ConnectorService {
    private final ConnectorRepository connectorRepository;

    @Override
    public List<Connector> getAllEnabledConnectors() {
        return connectorRepository.getAllEnabledConnectors();
    }
}
