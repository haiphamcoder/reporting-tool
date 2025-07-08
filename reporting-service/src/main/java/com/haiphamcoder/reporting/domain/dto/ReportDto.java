package com.haiphamcoder.reporting.domain.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.haiphamcoder.reporting.domain.enums.ReportPermissionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("config")
    private ReportConfig config;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("can_edit")
    private Boolean canEdit;

    @JsonProperty("can_share")
    private Boolean canShare;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserReportPermission {

        @JsonProperty("id")
        private String userId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("avatar")
        private String avatar;

        @JsonProperty("permission")
        private ReportPermissionType permission;

    }

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
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReportConfig {

        @JsonProperty("blocks")
        private List<Block> blocks;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Block {

            @JsonProperty("id")
            private String id;

            @JsonProperty("type")
            private BlockType type;

            @JsonProperty("content")
            private BlockContent content;

            @AllArgsConstructor
            public static enum BlockType {
                CHART("chart"),
                TEXT("text");

                @Getter
                @JsonValue
                private String value;

                public static BlockType fromValue(String value) {
                    return Arrays.stream(BlockType.values())
                            .filter(type -> type.value.equals(value))
                            .findFirst()
                            .orElse(null);
                }
            }

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class BlockContent {

                @JsonProperty("text")
                private String text;

                @JsonProperty("format")
                private TextBlockFormat format;

                @JsonProperty("chart_id")
                private String chartId;

                @JsonProperty("chart")
                private ChartDto chart;

                @Data
                @Builder
                @NoArgsConstructor
                @AllArgsConstructor
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class TextBlockFormat {

                    @JsonProperty("font_family")
                    private String fontFamily;

                    @JsonProperty("font_size")
                    private Integer fontSize;

                    @JsonProperty("font_weight")
                    private FontWeight fontWeight;

                    @JsonProperty("font_style")
                    private FontStyle fontStyle;

                    @JsonProperty("text_align")
                    private TextAlign textAlign;

                    @JsonProperty("color")
                    private String color;

                    @JsonProperty("background_color")
                    private String backgroundColor;

                    @JsonProperty("underline")
                    private Boolean underline;

                    @JsonProperty("strike_through")
                    private Boolean strikeThrough;

                    @AllArgsConstructor
                    public static enum FontWeight {
                        NORMAL("normal"),
                        BOLD("bold");

                        @Getter
                        @JsonValue
                        private String value;

                        public static FontWeight fromValue(String value) {
                            return Arrays.stream(FontWeight.values())
                                    .filter(weight -> weight.value.equals(value))
                                    .findFirst()
                                    .orElse(null);
                        }
                    }

                    @AllArgsConstructor
                    public static enum FontStyle {
                        NORMAL("normal"),
                        ITALIC("italic");

                        @Getter
                        @JsonValue
                        private String value;

                        public static FontStyle fromValue(String value) {
                            return Arrays.stream(FontStyle.values())
                                    .filter(style -> style.value.equals(value))
                                    .findFirst()
                                    .orElse(null);
                        }
                    }

                    @AllArgsConstructor
                    public static enum TextAlign {
                        LEFT("left"),
                        CENTER("center"),
                        RIGHT("right"),
                        JUSTIFY("justify");

                        @Getter
                        @JsonValue
                        private String value;

                        public static TextAlign fromValue(String value) {
                            return Arrays.stream(TextAlign.values())
                                    .filter(align -> align.value.equals(value))
                                    .findFirst()
                                    .orElse(null);
                        }
                    }
                }

            }

        }

    }

}
