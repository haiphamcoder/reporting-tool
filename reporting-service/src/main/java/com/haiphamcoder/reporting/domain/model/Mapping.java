package com.haiphamcoder.reporting.domain.model;

import lombok.Data;

@Data
public class Mapping {
    private String fieldName;
    private String fieldMapping;
    private String dataType;
    private String fieldAlias;
    private Integer hide;
    private String dimensionColumn;
    private String cumulativeQuery;
    private String linkChartId;
    private String linkFieldAlias;
    private String function;
    private String aggregation;

    public Mapping(String fieldName, String fieldMapping, String dataType, String fieldAlias, 
                  Integer hide, String dimensionColumn, String cumulativeQuery, String linkChartId,
                  String linkFieldAlias, String function, String aggregation) {
        this.fieldName = fieldName;
        this.fieldMapping = fieldMapping;
        this.dataType = dataType;
        this.fieldAlias = fieldAlias;
        this.hide = hide;
        this.dimensionColumn = dimensionColumn;
        this.cumulativeQuery = cumulativeQuery;
        this.linkChartId = linkChartId;
        this.linkFieldAlias = linkFieldAlias;
        this.function = function;
        this.aggregation = aggregation;
    }
} 