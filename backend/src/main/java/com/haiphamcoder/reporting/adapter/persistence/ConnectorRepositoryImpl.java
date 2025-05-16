package com.haiphamcoder.reporting.adapter.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.Connector;
import com.haiphamcoder.reporting.domain.repository.ConnectorRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ConnectorJpaRepository extends JpaRepository<Connector, Long> {

    List<Connector> findAllByEnabled(boolean enabled);

}

@Component
@RequiredArgsConstructor
public class ConnectorRepositoryImpl implements ConnectorRepository {
    private final ConnectorJpaRepository connectorJpaRepository;

    @Override
    public List<Connector> getAllEnabledConnectors() {
        return connectorJpaRepository.findAllByEnabled(true);
    }

}
