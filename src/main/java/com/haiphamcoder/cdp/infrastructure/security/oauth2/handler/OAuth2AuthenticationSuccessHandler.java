package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.haiphamcoder.cdp.domain.entity.User;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.error("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        User user = (User) authentication.getPrincipal();

        Map<String, String> tokens = jwtTokenProvider.generateTokens(authentication);
        String accessToken = tokens.get(OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue());
        String refreshToken = tokens.get(OAuth2AuthorizationRequestParams.REFRESH_TOKEN.getValue());
        
        CookieUtils.addCookie(response, "user-id", user.getId().toString());
        CookieUtils.addCookie(response, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue(), accessToken);
        CookieUtils.addCookie(response, OAuth2AuthorizationRequestParams.REFRESH_TOKEN.getValue(), refreshToken);

        clearAuthorizationRequestCookies(request, response);
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

        return redirectUri.orElseGet(() -> getDefaultTargetUrl());
    }      

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return redirectUriConfiguration.getAuthorizedRedirectUris().stream().anyMatch(
                authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return clientRedirectUri.equals(authorizedURI);
                });
    }

    private void clearAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue());
        CookieUtils.deleteCookie(request, response, "user-id");
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.ACCESS_TOKEN.getValue());
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.REFRESH_TOKEN.getValue());
    }
}
