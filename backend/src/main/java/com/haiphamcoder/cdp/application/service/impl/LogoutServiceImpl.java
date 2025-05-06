package com.haiphamcoder.cdp.application.service.impl;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiphamcoder.cdp.application.service.LogoutService;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.CookieUtils;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogoutServiceImpl implements LogoutService {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Delete the cookies
        CookieUtils.deleteCookie(request, response, CommonConstants.USER_ID_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, CommonConstants.ACCESS_TOKEN_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, CommonConstants.REFRESH_TOKEN_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME);

        // Set the response status and content type
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Create the response body
        RestAPIResponse<String> apiResponse = RestAPIResponse.ResponseFactory.createResponse("Logout successful");
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
        } catch (Exception e) {
            log.error("Error writing response: {}", e.getMessage());
        }
    }
}
