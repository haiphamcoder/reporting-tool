package com.haiphamcoder.reporting.domain.model.request;

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
public class ConfirmSheetRequest {

    @JsonProperty("sheet_name")
    private String sheetName;

    @JsonProperty("sheet_index")
    private Integer sheetIndex;

    @JsonProperty("data_range_selected")
    private String dataRangeSelected;
    
}
