package com.haiphamcoder.cdp.shared.http;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAPIResponse {
    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("data")
    private String data;

    @JsonProperty("headers")
    private Map<String, List<String>> headers;
}
