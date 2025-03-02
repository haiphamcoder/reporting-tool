package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.haiphamcoder.cdp.infrastructure.config.OAuth2AuthorizedRedirectUriConfiguration;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtTokenProvider;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
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
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
   
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2AuthenticationSuccessHandler onAuthenticationSuccess");
        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("Redirecting to: " + targetUrl);

        if (response.isCommitted()) {
            log.error("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Optional<String> redirectUri = CookieUtils
                .getCookie(request, OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue())
                .map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Unauthorized redirect URI: " + redirectUri.get());
        }

        String targetUrl = redirectUri.orElseGet(() -> getDefaultTargetUrl());
        Map<String, String> tokens = jwtTokenProvider.generateTokens(authentication);
        String accessToken = tokens.get(ACCESS_TOKEN);
        String refreshToken = tokens.get(REFRESH_TOKEN);
        return UriComponentsBuilder.fromUriString(targetUrl).queryParam(ACCESS_TOKEN, accessToken)
                .queryParam(REFRESH_TOKEN, refreshToken).build().toUriString();
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
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
