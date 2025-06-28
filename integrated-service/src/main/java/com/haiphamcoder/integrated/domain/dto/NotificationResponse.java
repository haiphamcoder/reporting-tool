package com.haiphamcoder.integrated.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class NotificationResponse {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("result")
    private NotificationResult result;

    @JsonProperty("message")
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationResult {
        private List<NotificationDto> notifications;
        private Long total;
        private Long unreadCount;
    }
}