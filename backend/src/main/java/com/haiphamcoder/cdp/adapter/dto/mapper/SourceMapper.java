package com.haiphamcoder.cdp.adapter.dto.mapper;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.domain.entity.Source;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SourceMapper {

    public static SourceDto toDto(Source source) {
        return SourceDto.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .connectorType(source.getConnectorType())
                .build();
    }

    public static Source toEntity(SourceDto sourceDto) {
        return Source.builder()
                .id(sourceDto.getId())
                .name(sourceDto.getName())
                .description(sourceDto.getDescription())
                .build();
    }
    
}
