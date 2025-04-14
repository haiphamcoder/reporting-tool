package com.haiphamcoder.cdp.adapter.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

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
    private Long id;
    private String name;
    private String description;
    private Integer typeConnector;
    private String config;
    private Integer status;
    private Long userId;
    private Long folderId;
    private Boolean isDeleted;
    private Boolean isStarred;
    private Timestamp lastSyncTime;
}
