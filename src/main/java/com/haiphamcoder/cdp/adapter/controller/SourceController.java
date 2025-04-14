package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/source")
@Tag(name = "source", description = "Source controller")
@RequiredArgsConstructor
public class SourceController {
    
}
