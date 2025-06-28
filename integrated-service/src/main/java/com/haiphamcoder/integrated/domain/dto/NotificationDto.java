package com.haiphamcoder.integrated.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("category")
    private String category;

    @JsonProperty("title")
    private String title;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("read")
    private Boolean read;

    @JsonProperty("actionUrl")
    private String actionUrl;

    @JsonProperty("userId")
    private String userId;
    
}
