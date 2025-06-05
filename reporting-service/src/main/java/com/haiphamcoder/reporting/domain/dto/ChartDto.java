package com.haiphamcoder.reporting.domain.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.reporting.domain.model.QueryOption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChartDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("config")
    private Map<String, Object> config;

    @JsonProperty("query_option")
    private QueryOption queryOption;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;
}
