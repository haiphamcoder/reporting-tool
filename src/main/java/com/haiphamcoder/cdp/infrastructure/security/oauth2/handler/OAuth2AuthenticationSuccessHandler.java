package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtTokenProvider;
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
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        User userPrincipal = (User) authentication.getPrincipal();

        log.info("Success OAuth2 authentication for '" + userPrincipal.getUsername() + "'");
        String targetUrl = determineTargetUrl(request, (UserDetails) userPrincipal);

        if (response.isCommitted()) {
            log.error("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        clearAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, UserDetails userPrincipal) {
        String targetUrl;
        targetUrl = CookieUtils.getCookie(request, OAuth2AuthorizationRequestParams.REGISTRATION_REDIRECT_URL)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Request param " + OAuth2AuthorizationRequestParams.REGISTRATION_REDIRECT_URL
                                    + " is required and cannot be empty"))
                    .getValue();
        String token = jwtTokenProvider.generateToken((UserDetails) userPrincipal);
        log.info("Generated token: " + token);
        return UriComponentsBuilder.fromUriString(targetUrl).fragment(token).build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

    private void clearAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.REGISTRATION_REDIRECT_URL);
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.SUCCESS_REDIRECT_URL);
        CookieUtils.deleteCookie(request, response, OAuth2AuthorizationRequestParams.FAILURE_REDIRECT_URL);
    }
}
