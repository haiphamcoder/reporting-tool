package com.haiphamcoder.integrated.mapper;

import com.haiphamcoder.integrated.domain.dto.NotificationDto;
import com.haiphamcoder.integrated.domain.entity.Notification;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class NotificationMapper {

    public static NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }
        
        return NotificationDto.builder()
                .id(entity.getId().toString())
                .type(entity.getType().name().toLowerCase())
                .category(entity.getCategory().name().toLowerCase())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .timestamp(entity.getTimestamp())
                .read(entity.getRead())
                .actionUrl(entity.getActionUrl())
                .userId(entity.getUserId())
                .build();
    }

    public static Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Notification.builder()
                .id(dto.getId() != null ? Long.parseLong(dto.getId()) : null)
                .type(Notification.NotificationType.valueOf(dto.getType().toUpperCase()))
                .category(Notification.NotificationCategory.valueOf(dto.getCategory().toUpperCase()))
                .title(dto.getTitle())
                .message(dto.getMessage())
                .timestamp(dto.getTimestamp())
                .read(dto.getRead())
                .actionUrl(dto.getActionUrl())
                .userId(dto.getUserId())
                .build();
    }

    public static List<NotificationDto> toDtoList(List<Notification> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<Notification> toEntityList(List<NotificationDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(NotificationMapper::toEntity)
                .collect(Collectors.toList());
    }
}


