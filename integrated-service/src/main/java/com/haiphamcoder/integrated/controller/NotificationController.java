package com.haiphamcoder.integrated.controller;

import com.haiphamcoder.integrated.domain.dto.*;
import com.haiphamcoder.integrated.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /notifications - Get all notifications for current user
     */
    @GetMapping
    public ResponseEntity<NotificationResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int limit) {

        String userId = notificationService.getCurrentUserId();
        log.info("Getting notifications for user: {}, page: {}, limit: {}", userId, page, limit);

        NotificationResponse response = notificationService.getNotifications(userId, page, limit);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /notifications/mark-read - Mark notifications as read
     */
    @PostMapping("/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@RequestBody MarkAsReadRequest request) {
        String userId = notificationService.getCurrentUserId();
        log.info("Marking notifications as read for user: {}, notificationIds: {}", userId,
                request.getNotificationIds());

        ApiResponse<Void> response = notificationService.markAsRead(userId, request.getNotificationIds());

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /notifications/mark-all-read - Mark all notifications as read
     */
    @PostMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        String userId = notificationService.getCurrentUserId();
        log.info("Marking all notifications as read for user: {}", userId);

        ApiResponse<Void> response = notificationService.markAllAsRead(userId);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * DELETE /notifications/{id} - Delete notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable String id) {
        String userId = notificationService.getCurrentUserId();
        log.info("Deleting notification {} for user: {}", id, userId);

        ApiResponse<Void> response = notificationService.deleteNotification(userId, id);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * DELETE /notifications/clear-all - Clear all notifications
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<ApiResponse<Void>> clearAllNotifications() {
        String userId = notificationService.getCurrentUserId();
        log.info("Clearing all notifications for user: {}", userId);

        ApiResponse<Void> response = notificationService.clearAllNotifications(userId);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /notifications/unread-count - Get unread count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        String userId = notificationService.getCurrentUserId();
        log.info("Getting unread count for user: {}", userId);

        ApiResponse<Long> response = notificationService.getUnreadCount(userId);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /notifications - Create notification (for testing or admin purposes)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDto>> createNotification(
            @RequestBody CreateNotificationRequest request) {
        log.info("Creating notification: {}", request);

        ApiResponse<NotificationDto> response = notificationService.createNotification(request);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
