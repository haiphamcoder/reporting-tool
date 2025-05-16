package com.haiphamcoder.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceFilter {
    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("connector_type")
    private Integer connectorType;

    @JsonProperty("folder_id")
    private Integer folderId;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("limit")
    private Integer limit;
}
