package com.haiphamcoder.cdp.shared.sse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SseEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("data")
    private Object data;

    @AllArgsConstructor
    public static enum Type {
        CONFIRM("confirm"),
        HEARTBEAT("heartbeat"),
        MESSAGE("message");

        @Getter
        private final String value;
    }

}
