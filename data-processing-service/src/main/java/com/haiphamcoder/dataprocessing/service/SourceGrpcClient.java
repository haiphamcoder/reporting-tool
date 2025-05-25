package com.haiphamcoder.dataprocessing.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;
import com.haiphamcoder.dataprocessing.shared.StringUtils;
import com.haiphamcoder.reporting.proto.*;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SourceGrpcClient {
    private final SourceServiceGrpc.SourceServiceBlockingStub sourceServiceBlockingStub;

    public SourceGrpcClient(@Qualifier("sourceServiceChannel") ManagedChannel sourceServiceChannel) {
        this.sourceServiceBlockingStub = SourceServiceGrpc.newBlockingStub(sourceServiceChannel);
    }

    public SourceDto getSourceById(Long id) {
        GetSourceByIdRequest request = GetSourceByIdRequest.newBuilder().setId(id).build();
        GetSourceByIdResponse response = sourceServiceBlockingStub.getSourceById(request);
        SourceProto source = response.getSource();
        return convertSourceProtoToSourceDto(source);
    }

    public SourceDto updateSource(SourceDto source) {
        UpdateSourceRequest request = UpdateSourceRequest.newBuilder().setSource(convertSourceDtoToSourceProto(source))
                .build();
        UpdateSourceResponse response = sourceServiceBlockingStub.updateSource(request);
        SourceProto updatedSource = response.getSource();
        return convertSourceProtoToSourceDto(updatedSource);
    }

    private SourceDto convertSourceProtoToSourceDto(SourceProto source) {
        try {
            return SourceDto.builder()
                    .id(source.getId())
                    .name(source.getName())
                    .description(source.getDescription())
                    .connectorType(source.getConnectorType())
                    .mapping(
                            StringUtils.isNullOrEmpty(source.getMapping()) ? new ArrayList<>()
                                    : MapperUtils.objectMapper.readValue(source.getMapping(),
                                            new TypeReference<List<SourceDto.Mapping>>() {
                                            }))
                    .config(
                            StringUtils.isNullOrEmpty(source.getConfig()) ? MapperUtils.objectMapper.createObjectNode()
                                    : MapperUtils.objectMapper.readValue(source.getConfig(), ObjectNode.class))
                    .tableName(source.getTableName())
                    .status(source.getStatus())
                    .userId(source.getUserId())
                    .isDeleted(source.getIsDeleted())
                    .isStarred(source.getIsStarred())
                    .lastSyncTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(source.getLastSyncTime()),
                            ZoneId.systemDefault()))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SourceProto convertSourceDtoToSourceProto(SourceDto source) {
        try {
            return SourceProto.newBuilder()
                    .setId(source.getId())
                    .setName(source.getName())
                    .setDescription(source.getDescription())
                    .setConnectorType(source.getConnectorType())
                    .setMapping(MapperUtils.objectMapper.writeValueAsString(source.getMapping()))
                    .setConfig(MapperUtils.objectMapper.writeValueAsString(source.getConfig()))
                    .setTableName(source.getTableName())
                    .setStatus(source.getStatus())
                    .setUserId(source.getUserId())
                    .setIsDeleted(source.getIsDeleted())
                    .setIsStarred(source.getIsStarred())
                    .setLastSyncTime(source.getLastSyncTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
