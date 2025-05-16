package com.haiphamcoder.reporting.domain.model;

import lombok.Data;
import java.util.List;

@Data
public class Dimension {
    private String dimensionType;
    private List<Value> values;

    @Data
    public static class Value {
        private String type;
        private String character;
        private String fieldName;
        private String aggregation;
    }
} 