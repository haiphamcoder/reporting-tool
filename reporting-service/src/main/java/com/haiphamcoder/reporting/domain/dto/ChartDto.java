package com.haiphamcoder.reporting.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.reporting.domain.enums.QueryType;
import com.haiphamcoder.reporting.domain.enums.ChartPermissionType;
import com.haiphamcoder.reporting.domain.enums.ChartType;
import com.haiphamcoder.reporting.domain.model.QueryOption;

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
public class ChartDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("config")
    private ChartConfig config;

    @JsonProperty("sql_query")
    private String sqlQuery;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;

    @JsonProperty("owner")
    private Owner owner;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("avatar")
        private String avatar;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserChartPermission {

        @JsonProperty("id")
        private Long userId;

        @JsonProperty("permission")
        private ChartPermissionType permission;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChartConfig {

        @JsonProperty("type")
        private ChartType type;

        @JsonProperty("bar_chart_config")
        private BarChartConfig barChartConfig;

        @JsonProperty("pie_chart_config")
        private PieChartConfig pieChartConfig;

        @JsonProperty("line_chart_config")
        private LineChartConfig lineChartConfig;

        @JsonProperty("area_chart_config")
        private AreaChartConfig areaChartConfig;

        @JsonProperty("table_config")
        private TableConfig tableConfig;

        @JsonProperty("mode")
        private QueryType mode;

        @JsonProperty("query_option")
        private QueryOption queryOption;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BarChartConfig {

            @JsonProperty("x_axis")
            private String xAxis;

            @JsonProperty("x_axis_label")
            private String xAxisLabel;

            @JsonProperty("y_axis")
            private String yAxis;

            @JsonProperty("y_axis_label")
            private String yAxisLabel;

            @JsonProperty("orientation")
            private String orientation; // "vertical", "horizontal"

            @JsonProperty("stacked")
            private Boolean stacked;

            @JsonProperty("colors")
            private List<String> colors;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PieChartConfig {

            @JsonProperty("label_field")
            private String labelField;

            @JsonProperty("value_field")
            private String valueField;

            @JsonProperty("show_percentage")
            private Boolean showPercentage;

            @JsonProperty("show_legend")
            private Boolean showLegend;

            @JsonProperty("colors")
            private List<String> colors;

            @JsonProperty("donut")
            private Boolean donut;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class LineChartConfig {

            @JsonProperty("x_axis")
            private String xAxis;

            @JsonProperty("x_axis_label")
            private String xAxisLabel;

            @JsonProperty("y_axis")
            private String yAxis;

            @JsonProperty("y_axis_label")
            private String yAxisLabel;

            @JsonProperty("smooth")
            private Boolean smooth;

            @JsonProperty("show_points")
            private Boolean showPoints;

            @JsonProperty("fill_area")
            private Boolean fillArea;

            @JsonProperty("colors")
            private List<String> colors;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AreaChartConfig {

            @JsonProperty("x_axis")
            private String xAxis;

            @JsonProperty("x_axis_label")
            private String xAxisLabel;

            @JsonProperty("y_axis")
            private String yAxis;

            @JsonProperty("y_axis_label")
            private String yAxisLabel;

            @JsonProperty("stacked")
            private Boolean stacked;

            @JsonProperty("opacity")
            private Double opacity;

            @JsonProperty("colors")
            private List<String> colors;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TableConfig {

            @JsonProperty("columns")
            private List<TableColumn> columns;

            @JsonProperty("show_header")
            private Boolean showHeader;

            @JsonProperty("striped")
            private Boolean striped;

            @JsonProperty("bordered")
            private Boolean bordered;

            @JsonProperty("pagination")
            private Boolean pagination;

            @JsonProperty("page_size")
            private Integer pageSize;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class TableColumn {
                @JsonProperty("field")
                private String field;

                @JsonProperty("header")
                private String header;

                @JsonProperty("sortable")
                private Boolean sortable;

                @JsonProperty("width")
                private String width;

                @JsonProperty("align")
                private String align; // "left", "center", "right"
            }
        }
    }
}
