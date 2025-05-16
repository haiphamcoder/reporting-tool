package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.entity.Connector;

public interface ConnectorService {

    public List<Connector> getAllEnabledConnectors();
}
