package com.haiphamcoder.reporting.mapper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.dto.SourceDto.Mapping;
import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.entity.Source.SourceBuilder;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SourceMapper {

    public static SourceDto toDto(Source source) {
        try {
            return SourceDto.builder()
                    .id(source.getId() != null ? source.getId() : null)
                    .name(source.getName() != null ? source.getName() : null)
                    .description(source.getDescription() != null ? source.getDescription() : null)
                    .connectorType(source.getConnectorType() != null ? source.getConnectorType() : null)
                    .tableName(source.getTableName() != null ? source.getTableName() : null)
                    .config(!StringUtils.isNullOrEmpty(source.getConfig())
                            ? MapperUtils.objectMapper.readValue(source.getConfig(), ObjectNode.class)
                            : MapperUtils.objectMapper.createObjectNode())
                    .mapping(!StringUtils.isNullOrEmpty(source.getMapping())
                            ? MapperUtils.objectMapper
                                    .readValue(source.getMapping(), new TypeReference<List<Mapping>>() {
                                    })
                            : new ArrayList<>())
                    .status(source.getStatus() != null ? source.getStatus() : null)
                    .userId(source.getUserId() != null ? source.getUserId() : null)
                    .isDeleted(source.getIsDeleted() != null ? source.getIsDeleted() : null)
                    .isStarred(source.getIsStarred() != null ? source.getIsStarred() : null)
                    .lastSyncTime(source.getLastSyncTime() != null ? source.getLastSyncTime() : null)
                    .createdAt(source.getCreatedAt() != null ? source.getCreatedAt() : null)
                    .modifiedAt(source.getModifiedAt() != null ? source.getModifiedAt() : null)
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Source toEntity(SourceDto sourceDto) {
        SourceBuilder builder = Source.builder();
        if (sourceDto.getId() != null) {
            builder.id(sourceDto.getId());
        }
        if (!StringUtils.isNullOrEmpty(sourceDto.getName())) {
            builder.name(sourceDto.getName());
        }
        if (!StringUtils.isNullOrEmpty(sourceDto.getDescription())) {
            builder.description(sourceDto.getDescription());
        }
        if (sourceDto.getConnectorType() != null) {
            builder.connectorType(sourceDto.getConnectorType());
        }
        if (!StringUtils.isNullOrEmpty(sourceDto.getTableName())) {
            builder.tableName(sourceDto.getTableName());
        }
        if (sourceDto.getConfig() != null) {
            try {
                builder.config(MapperUtils.objectMapper.writeValueAsString(sourceDto.getConfig()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if (sourceDto.getMapping() != null) {
            try {
                builder.mapping(MapperUtils.objectMapper.writeValueAsString(sourceDto.getMapping()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if (sourceDto.getStatus() != null) {
            builder.status(sourceDto.getStatus());
        }
        if (sourceDto.getUserId() != null) {
            builder.userId(sourceDto.getUserId());
        }
        if (sourceDto.getIsDeleted() != null) {
            builder.isDeleted(sourceDto.getIsDeleted());
        }
        if (sourceDto.getIsStarred() != null) {
            builder.isStarred(sourceDto.getIsStarred());
        }
        if (sourceDto.getLastSyncTime() != null) {
            builder.lastSyncTime(sourceDto.getLastSyncTime());
        }
        if (sourceDto.getCreatedAt() != null) {
            builder.createdAt(sourceDto.getCreatedAt());
        }
        if (sourceDto.getModifiedAt() != null) {
            builder.modifiedAt(sourceDto.getModifiedAt());
        }
        return builder.build();
    }

    public static SourceDto updateSourceDto(Source source, SourceDto sourceDto) {
        SourceDto result = toDto(source);
        if (sourceDto.getId() != null) {
            result.setId(sourceDto.getId());
        }
        if (!StringUtils.isNullOrEmpty(sourceDto.getName())) {
            result.setName(sourceDto.getName());
        }
        if (!StringUtils.isNullOrEmpty(sourceDto.getDescription())) {
            result.setDescription(sourceDto.getDescription());
        }
        if (sourceDto.getConnectorType() != null) {
            result.setConnectorType(sourceDto.getConnectorType());
        }
        if (!StringUtils.isNullOrEmpty(sourceDto.getTableName())) {
            result.setTableName(sourceDto.getTableName());
        }
        if (sourceDto.getConfig() != null) {
            result.setConfig(sourceDto.getConfig());
        }
        if (sourceDto.getMapping() != null) {
            result.setMapping(sourceDto.getMapping());
        }
        if (sourceDto.getStatus() != null) {
            result.setStatus(sourceDto.getStatus());
        }
        if (sourceDto.getUserId() != null) {
            result.setUserId(sourceDto.getUserId());
        }
        if (sourceDto.getIsDeleted() != null) {
            result.setIsDeleted(sourceDto.getIsDeleted());
        }
        if (sourceDto.getIsStarred() != null) {
            result.setIsStarred(sourceDto.getIsStarred());
        }
        if (sourceDto.getLastSyncTime() != null) {
            result.setLastSyncTime(sourceDto.getLastSyncTime());
        }
        return result;
    }

}
