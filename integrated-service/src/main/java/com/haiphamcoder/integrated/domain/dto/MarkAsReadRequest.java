package com.haiphamcoder.integrated.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadRequest {
    private List<String> notificationIds;
} 