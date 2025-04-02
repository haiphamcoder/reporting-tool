package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import com.haiphamcoder.cdp.domain.entity.Connector;

public interface ConnectorRepository {

    List<Connector> getAllEnabledConnectors();

}
