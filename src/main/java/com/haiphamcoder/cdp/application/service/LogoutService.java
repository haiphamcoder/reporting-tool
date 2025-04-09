package com.haiphamcoder.cdp.application.service;

import java.util.Arrays;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextHolder.clearContext();

        String[] cookiesToDelete = {"user-id", "access-token", "refresh-token"};
        Arrays.stream(cookiesToDelete).forEach(cookieName -> {
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            if (cookie != null) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        });

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        RestAPIResponse<String> apiResponse = RestAPIResponse.ResponseFactory.createResponse("Logout successful");
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
        } catch (Exception e) {
            log.error("Error writing response: {}", e.getMessage());
        }
    }

}
