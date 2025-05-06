package com.haiphamcoder.cdp.application.service;

import java.util.List;

import com.haiphamcoder.cdp.domain.entity.Connector;

public interface ConnectorService {

    public List<Connector> getAllEnabledConnectors();
}
