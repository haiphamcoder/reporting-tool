package com.haiphamcoder.integrated.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private String type;
    private String category;
    private String title;
    private String message;
    private String actionUrl;
    private String userId;
} 