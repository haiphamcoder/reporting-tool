package com.haiphamcoder.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PreviewDataRequest {
    @JsonProperty("connector_type")
    private Integer connectorType;

    @JsonProperty("path")
    private String path;

    @JsonProperty("limit")
    @Builder.Default
    private Integer limit = 20;
}
