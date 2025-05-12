package com.haiphamcoder.cdp.adapter.dto.mapper;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.shared.MapperUtils;

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
                    .config(source.getConfig() != null ? source.getConfig() : null)
                    .mapping(source.getMapping() != null ? MapperUtils.objectMapper
                            .readValue(source.getMapping().toString(), new TypeReference<List<Mapping>>() {
                            }) : null)
                    .status(source.getStatus() != null ? source.getStatus() : null)
                    .userId(source.getUser().getId() != null ? source.getUser().getId() : null)
                    .folderId(source.getFolder() != null ? source.getFolder().getId() : null)
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
