package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.haiphamcoder.cdp.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.OAuth2AuthorizationRequestParams;
import com.haiphamcoder.cdp.shared.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, exception);
        clearAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, AuthenticationException exception) {
        String targetUrl = CookieUtils.getCookie(request, OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Request param "
                        + OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue() + " is required and cannot be empty"))
                .getValue();
        log.info("Redirecting to: " + targetUrl);
        String errorCode = "error.unknownError";
        return UriComponentsBuilder.fromUriString(targetUrl).queryParam("error", errorCode).build().toUriString();
    }

    private void clearAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
