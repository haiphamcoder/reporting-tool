package com.haiphamcoder.dataprocessing.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.config.CommonConstants;
import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.exception.ConnectorTypeNotSupportException;
import com.haiphamcoder.dataprocessing.domain.exception.SourceNotFoundException;
import com.haiphamcoder.dataprocessing.service.CSVProcessingService;
import com.haiphamcoder.dataprocessing.service.SchemaSourceService;
import com.haiphamcoder.dataprocessing.service.SourceGrpcClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchemaSourceServiceImpl implements SchemaSourceService {

    private final SourceGrpcClient sourceGrpcClient;
    private final CSVProcessingService csvProcessingService;

    @Override
    public List<Mapping> getSchema(Long sourceId) {
        SourceDto source = sourceGrpcClient.getSourceById(sourceId);

        if (source == null || source.getConfig() == null){
            throw new SourceNotFoundException("Source not found");
        }

        switch (source.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV:
                return csvProcessingService.getSchema(source);

            default:
                throw new ConnectorTypeNotSupportException("Connector type not supported");
        }
    }

}
