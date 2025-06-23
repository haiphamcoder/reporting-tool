package com.haiphamcoder.dataprocessing.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetChartPreviewDataRequest {

    @JsonProperty("sql_query")
    private String sqlQuery;

    @JsonProperty("fields")
    private List<Field> fields;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {
        @JsonProperty("field_name")
        private String fieldName;

        @JsonProperty("data_type")
        private String dataType;

        @JsonProperty("alias")
        private String alias;
    }

}
