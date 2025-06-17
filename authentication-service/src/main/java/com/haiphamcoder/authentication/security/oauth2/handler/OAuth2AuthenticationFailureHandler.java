package com.haiphamcoder.authentication.security.oauth2.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.haiphamcoder.authentication.security.oauth2.OAuth2AuthorizationRequestParams;
import com.haiphamcoder.authentication.shared.CookieUtils;

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
        exception.printStackTrace();
        String targetUrl = determineTargetUrl(request);
        clearAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request) {
        String targetUrl = CookieUtils.getCookie(request, OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Request param "
                        + OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue() + " is required and cannot be empty"))
                .getValue();
        String errorCode = "error.unknownError";
        return UriComponentsBuilder.fromUriString(targetUrl).queryParam("error", errorCode).build().toUriString();
    }

    private void clearAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue());
        CookieUtils.deleteCookie(request, response, "user-id");
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue());
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.REFRESH_TOKEN.getValue());
    }
}
