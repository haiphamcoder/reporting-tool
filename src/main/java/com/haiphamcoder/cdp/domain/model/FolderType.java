package com.haiphamcoder.cdp.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FolderType {
    SOURCE("SOURCE"),
    REPORT("REPORT"),
    CHART("CHART");

    @Getter
    private final String value;
    
}
