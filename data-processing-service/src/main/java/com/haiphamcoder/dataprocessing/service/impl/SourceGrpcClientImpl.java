package com.haiphamcoder.dataprocessing.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto.SourceDtoBuilder;
import com.haiphamcoder.dataprocessing.service.SourceGrpcClient;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;
import com.haiphamcoder.dataprocessing.shared.StringUtils;
import com.haiphamcoder.reporting.proto.GetSourceByIdRequest;
import com.haiphamcoder.reporting.proto.GetSourceByIdResponse;
import com.haiphamcoder.reporting.proto.SourceProto;
import com.haiphamcoder.reporting.proto.SourceServiceGrpc;
import com.haiphamcoder.reporting.proto.UpdateSourceRequest;
import com.haiphamcoder.reporting.proto.UpdateSourceResponse;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SourceGrpcClientImpl implements SourceGrpcClient {
    private final SourceServiceGrpc.SourceServiceBlockingStub sourceServiceBlockingStub;

    public SourceGrpcClientImpl(@Qualifier("sourceServiceChannel") ManagedChannel sourceServiceChannel) {
        this.sourceServiceBlockingStub = SourceServiceGrpc.newBlockingStub(sourceServiceChannel);
    }

    @Override
    public SourceDto getSourceById(Long id) {
        GetSourceByIdRequest request = GetSourceByIdRequest.newBuilder().setId(id).build();
        GetSourceByIdResponse response = sourceServiceBlockingStub.getSourceById(request);
        SourceProto source = response.getSource();
        return convertSourceProtoToSourceDto(source);
    }

    @Override
    public SourceDto updateSource(SourceDto source) {
        UpdateSourceRequest request = UpdateSourceRequest.newBuilder().setSource(convertSourceDtoToSourceProto(source))
                .build();
        UpdateSourceResponse response = sourceServiceBlockingStub.updateSource(request);
        SourceProto updatedSource = response.getSource();
        return convertSourceProtoToSourceDto(updatedSource);
    }

    private SourceDto convertSourceProtoToSourceDto(SourceProto source) {

        try {

            SourceDtoBuilder builder = SourceDto.builder();
            builder.id(source.getId());
            if (!StringUtils.isNullOrEmpty(source.getName())) {
                builder.name(source.getName());
            }
            if (!StringUtils.isNullOrEmpty(source.getDescription())) {
                builder.description(source.getDescription());
            }
            builder.connectorType(source.getConnectorType());
            if (!StringUtils.isNullOrEmpty(source.getMapping())) {
                builder.mapping(MapperUtils.objectMapper.readValue(source.getMapping(),
                        new TypeReference<List<Mapping>>() {
                        }));
            }
            if (!StringUtils.isNullOrEmpty(source.getConfig())) {
                builder.config(MapperUtils.objectMapper.readValue(source.getConfig(), ObjectNode.class));
            }
            if (!StringUtils.isNullOrEmpty(source.getTableName())) {
                builder.tableName(source.getTableName());
            }
            builder.status(source.getStatus());
            builder.userId(source.getUserId());
            builder.isDeleted(source.getIsDeleted());
            builder.isStarred(source.getIsStarred());
            builder.lastSyncTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(source.getLastSyncTime()),
                    ZoneId.systemDefault()));
            return builder.build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SourceProto convertSourceDtoToSourceProto(SourceDto source) {
        try {

            SourceProto.Builder builder = SourceProto.newBuilder();
            builder.setId(source.getId());
            if (!StringUtils.isNullOrEmpty(source.getName())) {
                builder.setName(source.getName());
            }
            if (!StringUtils.isNullOrEmpty(source.getDescription())) {
                builder.setDescription(source.getDescription());
            }
            builder.setConnectorType(source.getConnectorType());
            if (source.getMapping() != null && !source.getMapping().isEmpty()) {
                builder.setMapping(MapperUtils.objectMapper.writeValueAsString(source.getMapping()));
            }
            if (source.getConfig() != null && !source.getConfig().isEmpty()) {
                builder.setConfig(MapperUtils.objectMapper.writeValueAsString(source.getConfig()));
            }
            if (!StringUtils.isNullOrEmpty(source.getTableName())) {
                builder.setTableName(source.getTableName());
            }
            builder.setStatus(source.getStatus());
            builder.setUserId(source.getUserId());
            builder.setIsDeleted(source.getIsDeleted());
            builder.setIsStarred(source.getIsStarred());
            builder.setLastSyncTime(source.getLastSyncTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            return builder.build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
