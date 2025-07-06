package com.haiphamcoder.reporting.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
public class SourceDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("connector_type")
    private Integer connectorType;

    @JsonProperty("table_name")
    private String tableName;

    @JsonProperty("config")
    private ObjectNode config;

    @JsonProperty("mapping")
    private List<Mapping> mapping;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("is_starred")
    private Boolean isStarred;

    @JsonProperty("last_sync_time")
    private LocalDateTime lastSyncTime;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;

    @JsonProperty("can_edit")
    private Boolean canEdit;

    @JsonProperty("can_share")
    private Boolean canShare;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mapping {

        @JsonProperty("field_name")
        private String fieldName;

        @JsonProperty("field_mapping")
        private String fieldMapping;

        @JsonProperty("field_type")
        private String fieldType;

        @JsonProperty("is_hidden")
        @Builder.Default
        private Boolean isHidden = false;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("avatar")
        private String avatar;

    }
}
