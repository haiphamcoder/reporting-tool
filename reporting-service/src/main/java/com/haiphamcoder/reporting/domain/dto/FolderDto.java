package com.haiphamcoder.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class FolderDto {

    @JsonProperty("id")
    @Builder.Default
    private Long id = null;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("parent_id")
    private Long parentFolderId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @JsonProperty("is_starred")
    @Builder.Default
    private Boolean isStarred = false;
}
