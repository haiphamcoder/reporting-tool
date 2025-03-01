package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.haiphamcoder.cdp.infrastructure.config.OAuth2AuthorizedRedirectUriConfiguration;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtTokenProvider;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.OAuth2AuthorizationRequestParams;
import com.haiphamcoder.cdp.shared.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2AuthorizedRedirectUriConfiguration redirectUriConfiguration;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("Redirecting to: " + targetUrl);

        if (response.isCommitted()) {
            log.error("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        Map<String, String> jwtTokens = jwtTokenProvider.generateTokens(authentication);
        String accessToken = jwtTokens.get(ACCESS_TOKEN_COOKIE_NAME);
        String refreshToken = jwtTokens.get(REFRESH_TOKEN_COOKIE_NAME);
        CookieUtils.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken);
        CookieUtils.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        clearAuthenticationAttributes(request, response);
        clearAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Optional<String> successRedirectUri = CookieUtils
                .getCookie(request, OAuth2AuthorizationRequestParams.SUCCESS_REDIRECT_URI.getValue())
                .map(Cookie::getValue);
        if (successRedirectUri.isPresent() && !isAuthorizedRedirectUri(successRedirectUri.get())) {
            throw new IllegalArgumentException("Unauthorized redirect URI: " + successRedirectUri.get());
        }

        return successRedirectUri.orElseGet(() -> getDefaultTargetUrl());
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return redirectUriConfiguration.getAuthorizedRedirectUris().stream().anyMatch(
                authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return clientRedirectUri.equals(authorizedURI);
                });

    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

    private void clearAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.SUCCESS_REDIRECT_URI.getValue());
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.FAILURE_REDIRECT_URI.getValue());
    }
}
