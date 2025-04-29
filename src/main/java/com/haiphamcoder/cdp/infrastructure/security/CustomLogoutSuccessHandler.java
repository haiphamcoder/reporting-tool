package com.haiphamcoder.cdp.infrastructure.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * Logout success handler
     * 
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Invalidate the session
        request.getSession().invalidate();

        // Delete the cookies
        String redirectUri = CookieUtils.getCookie(request, CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        // Build the target URL
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .build()
                .toUriString();

        // Redirect to the target URL
        response.sendRedirect(targetUrl);
    }

}
