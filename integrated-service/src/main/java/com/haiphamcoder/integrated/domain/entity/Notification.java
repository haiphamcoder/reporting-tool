package com.haiphamcoder.integrated.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationCategory category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean read = false;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "user_id")
    private String userId;

    public enum NotificationType {
        SUCCESS, ERROR, WARNING, INFO
    }

    public enum NotificationCategory {
        REPORT, CHART, SOURCE, USER, SYSTEM
    }
}
