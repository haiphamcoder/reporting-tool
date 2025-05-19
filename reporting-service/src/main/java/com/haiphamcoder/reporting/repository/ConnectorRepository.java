package com.haiphamcoder.reporting.repository;

import java.util.List;

import com.haiphamcoder.reporting.domain.entity.Connector;

public interface ConnectorRepository {

    List<Connector> getAllEnabledConnectors();

}
