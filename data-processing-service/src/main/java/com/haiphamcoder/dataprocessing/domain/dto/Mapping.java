package com.haiphamcoder.dataprocessing.domain.dto;

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
public class Mapping {

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
