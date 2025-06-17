package com.haiphamcoder.reporting.shared;

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
public class CookieProperties {
    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonProperty("domain")
    @Builder.Default
    private String domain = null;

    @JsonProperty("path")
    @Builder.Default
    private String path = "/";

    @JsonProperty("secure")
    @Builder.Default
    private boolean secure = false;

    @JsonProperty("http_only")
    @Builder.Default
    private boolean httpOnly = true;

    @JsonProperty("max_age")
    @Builder.Default
    private int maxAge = 604800; // 7 days

    @JsonProperty("same_site")
    @Builder.Default
    private boolean sameSite = false;
}
