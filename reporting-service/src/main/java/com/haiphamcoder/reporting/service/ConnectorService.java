package com.haiphamcoder.reporting.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.entity.Connector;

public interface ConnectorService {

    public List<Connector> getAllEnabledConnectors();
}
