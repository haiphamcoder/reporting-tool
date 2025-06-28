package com.haiphamcoder.integrated.service.impl;

import com.haiphamcoder.integrated.domain.dto.*;
import com.haiphamcoder.integrated.domain.entity.Notification;
import com.haiphamcoder.integrated.mapper.NotificationMapper;
import com.haiphamcoder.integrated.repository.NotificationRepository;
import com.haiphamcoder.integrated.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse getNotifications(String userId, int page, int limit) {
        try {
            Pageable pageable = PageRequest.of(page, limit);
            Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByTimestampDesc(userId,
                    pageable);

            List<NotificationDto> notifications = NotificationMapper.toDtoList(notificationPage.getContent());
            Long unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);

            return NotificationResponse.builder()
                    .success(true)
                    .result(NotificationResponse.NotificationResult.builder()
                            .notifications(notifications)
                            .total(notificationPage.getTotalElements())
                            .unreadCount(unreadCount)
                            .build())
                    .message("Notifications retrieved successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error getting notifications for user {}: {}", userId, e.getMessage());
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to retrieve notifications: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAsRead(String userId, List<String> notificationIds) {
        try {
            List<Long> ids = notificationIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // Verify that notifications belong to the user
            List<Notification> notifications = notificationRepository.findByIdIn(ids);
            boolean allBelongToUser = notifications.stream()
                    .allMatch(n -> userId.equals(n.getUserId()));

            if (!allBelongToUser) {
                return ApiResponse.<Void>builder()
                        .success(false)
                        .message("Some notifications do not belong to the current user")
                        .build();
            }

            notificationRepository.markAsReadByIds(ids);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Notifications marked as read successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error marking notifications as read for user {}: {}", userId, e.getMessage());
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to mark notifications as read: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAllAsRead(String userId) {
        try {
            notificationRepository.markAllAsReadByUserId(userId);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("All notifications marked as read successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error marking all notifications as read for user {}: {}", userId, e.getMessage());
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to mark all notifications as read: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteNotification(String userId, String notificationId) {
        try {
            Long id = Long.parseLong(notificationId);
            Notification notification = notificationRepository.findById(id)
                    .orElse(null);

            if (notification == null) {
                return ApiResponse.<Void>builder()
                        .success(false)
                        .message("Notification not found")
                        .build();
            }

            if (!userId.equals(notification.getUserId())) {
                return ApiResponse.<Void>builder()
                        .success(false)
                        .message("Notification does not belong to the current user")
                        .build();
            }

            notificationRepository.deleteById(id);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Notification deleted successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error deleting notification {} for user {}: {}", notificationId, userId, e.getMessage());
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete notification: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> clearAllNotifications(String userId) {
        try {
            notificationRepository.deleteByUserId(userId);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("All notifications cleared successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error clearing all notifications for user {}: {}", userId, e.getMessage());
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to clear all notifications: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Long> getUnreadCount(String userId) {
        try {
            Long unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);

            return ApiResponse.<Long>builder()
                    .success(true)
                    .result(unreadCount)
                    .message("Unread count retrieved successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error getting unread count for user {}: {}", userId, e.getMessage());
            return ApiResponse.<Long>builder()
                    .success(false)
                    .message("Failed to get unread count: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<NotificationDto> createNotification(CreateNotificationRequest request) {
        try {
            Notification notification = Notification.builder()
                    .type(Notification.NotificationType.valueOf(request.getType().toUpperCase()))
                    .category(Notification.NotificationCategory.valueOf(request.getCategory().toUpperCase()))
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .actionUrl(request.getActionUrl())
                    .userId(request.getUserId())
                    .build();

            Notification savedNotification = notificationRepository.save(notification);
            NotificationDto notificationDto = NotificationMapper.toDto(savedNotification);

            return ApiResponse.<NotificationDto>builder()
                    .success(true)
                    .result(notificationDto)
                    .message("Notification created successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error creating notification: {}", e.getMessage());
            return ApiResponse.<NotificationDto>builder()
                    .success(false)
                    .message("Failed to create notification: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public String getCurrentUserId() {
        // TODO: Implement based on your authentication mechanism
        // This is a placeholder - you should implement this based on your security
        // setup
        // For example, if using Spring Security with JWT:
        // return SecurityContextHolder.getContext().getAuthentication().getName();

        // For now, return a default user ID for testing
        return "default-user";
    }
}
