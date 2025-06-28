package com.haiphamcoder.integrated.service;

import com.haiphamcoder.integrated.domain.dto.*;

import java.util.List;

public interface NotificationService {
    
    // Get notifications with pagination
    NotificationResponse getNotifications(String userId, int page, int limit);
    
    // Mark notifications as read
    ApiResponse<Void> markAsRead(String userId, List<String> notificationIds);
    
    // Mark all notifications as read
    ApiResponse<Void> markAllAsRead(String userId);
    
    // Delete notification
    ApiResponse<Void> deleteNotification(String userId, String notificationId);
    
    // Clear all notifications
    ApiResponse<Void> clearAllNotifications(String userId);
    
    // Get unread count
    ApiResponse<Long> getUnreadCount(String userId);
    
    // Create notification
    ApiResponse<NotificationDto> createNotification(CreateNotificationRequest request);
    
    // Get current user ID (to be implemented based on authentication)
    String getCurrentUserId();
}
