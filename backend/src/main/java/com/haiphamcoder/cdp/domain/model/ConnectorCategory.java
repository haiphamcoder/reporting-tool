package com.haiphamcoder.cdp.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConnectorCategory {
    FILE("FILE"),
    DATABASE("DATABASE");

    @Getter
    private final String value;
}
