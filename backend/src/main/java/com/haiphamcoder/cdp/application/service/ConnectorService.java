package com.haiphamcoder.cdp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.Connector;
import com.haiphamcoder.cdp.domain.repository.ConnectorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectorService {
    private final ConnectorRepository connectorRepository;

    public List<Connector> getAllEnabledConnectors() {
        return connectorRepository.getAllEnabledConnectors();
    }
}
