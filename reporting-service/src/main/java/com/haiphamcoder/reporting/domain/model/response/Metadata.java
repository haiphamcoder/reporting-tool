package com.haiphamcoder.reporting.domain.model.response;

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
public class Metadata {

    @JsonProperty("total_elements")
    @Builder.Default
    private Long totalElements = 0L;

    @JsonProperty("number_of_elements")
    @Builder.Default
    private Integer numberOfElements = 0;

    @JsonProperty("total_pages")
    @Builder.Default
    private Integer totalPages = 0;

    @JsonProperty("current_page")
    @Builder.Default
    private Integer currentPage = 0;

    @JsonProperty("page_size")
    @Builder.Default
    private Integer pageSize = 0;

}
