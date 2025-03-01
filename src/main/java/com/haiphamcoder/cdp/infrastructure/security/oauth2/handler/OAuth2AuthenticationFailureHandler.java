package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.haiphamcoder.cdp.infrastructure.security.oauth2.OAuth2AuthorizationRequestParams;
import com.haiphamcoder.cdp.shared.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, exception);
        clearAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, AuthenticationException exception) {
        String targetUrl = CookieUtils.getCookie(request, OAuth2AuthorizationRequestParams.FAILURE_REDIRECT_URI.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Request param "
                        + OAuth2AuthorizationRequestParams.FAILURE_REDIRECT_URI.getValue() + " is required and cannot be empty"))
                .getValue();
        String errorCode = "error.unknownError";
        return UriComponentsBuilder.fromUriString(targetUrl).fragment(errorCode).build().toUriString();
    }

    private void clearAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.SUCCESS_REDIRECT_URI.getValue());
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.FAILURE_REDIRECT_URI.getValue());
    }
}
