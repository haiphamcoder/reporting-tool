package com.haiphamcoder.reporting.mapper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.dto.SourceDto.Mapping;
import com.haiphamcoder.reporting.domain.entity.Source;
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
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Source toEntity(SourceDto sourceDto) {
        return Source.builder()
                .id(sourceDto.getId())
                .name(sourceDto.getName())
                .description(sourceDto.getDescription())
                .build();
    }

}
