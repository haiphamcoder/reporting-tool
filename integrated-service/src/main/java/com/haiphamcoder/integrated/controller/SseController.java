package com.haiphamcoder.integrated.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.haiphamcoder.integrated.config.CommonConstants;
import com.haiphamcoder.integrated.service.ServerSentEventsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sse")
@Slf4j
@RequiredArgsConstructor
public class SseController {

    private final ServerSentEventsService serverSentEventsService;

    /**
     * Get resource status SSE emitter. This is used to get the resource status.
     * 
     * @param userId  User ID
     * @param timeout Timeout in milliseconds
     * @return SseEmitter
     */
    @GetMapping(path = "/resource-status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getResourceStatus(@CookieValue("user-id") Long userId,
            @RequestParam(required = false, defaultValue = "0") Long timeout) {
        return serverSentEventsService.add(userId, CommonConstants.SSE_EVENT_TYPE_RESOURCE_STATUS, timeout);
    }

}
