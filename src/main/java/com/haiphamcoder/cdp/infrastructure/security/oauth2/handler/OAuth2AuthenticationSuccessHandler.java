package com.haiphamcoder.cdp.infrastructure.security.oauth2.handler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.repository.UserRepository;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.infrastructure.config.OAuth2AuthorizedRedirectUriConfiguration;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtTokenProvider;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.haiphamcoder.cdp.shared.CookieUtils;
import com.haiphamcoder.cdp.shared.StringUtils;

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
    private final UserRepository userRepository;

    /**
     * On authentication success
     * 
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Determine the target URL
        String targetUrl = determineTargetUrl(request, response, authentication);        
        if (StringUtils.isNullOrEmpty(targetUrl)) {
            throw new BadRequestException("Unable to redirect to target URL");
        }
        if (!isAuthorizedRedirectUri(targetUrl)) {
            log.error("Target URL is not authorized: {}", targetUrl);
            throw new BadRequestException("Unable to redirect to target URL");
        }

        // If the response has already been committed, return
        if (response.isCommitted()) {
            log.error("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        // Get the OAuth2 user
        OAuth2User oAuth2User = getOAuth2User(authentication);
        log.info("OAuth2 user: {}", oAuth2User);
        if (oAuth2User == null) {
            log.error("OAuth2 user is null");
            throw new BadRequestException("Unable to get OAuth2 user");
        }

        // Get the user from the repository using the email
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            log.error("Email not found in OAuth2 user attributes");
            throw new BadRequestException("Email not found in OAuth2 user attributes");
        }

        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found for email: " + email));

        // Generate the tokens
        Map<String, String> tokens = jwtTokenProvider.generateTokens(authentication);
        String accessToken = tokens.get(CommonConstants.ACCESS_TOKEN_COOKIE_NAME);
        String refreshToken = tokens.get(CommonConstants.REFRESH_TOKEN_COOKIE_NAME);

        // Add the cookies
        CookieUtils.addCookie(response, CommonConstants.USER_ID_COOKIE_NAME, user.getId().toString());
        CookieUtils.addCookie(response, CommonConstants.ACCESS_TOKEN_COOKIE_NAME, accessToken);
        CookieUtils.addCookie(response, CommonConstants.REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        // Clear the authentication attributes
        clearAuthenticationAttributes(request, response);

        // Redirect to the target URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determine the target URL
     * 
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @return the target URL
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Optional<String> redirectUri = CookieUtils
                .getCookie(request, CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        return redirectUri.orElseGet(() -> getDefaultTargetUrl());
    }

    /**
     * Check if the redirect URI is authorized
     * 
     * @param uri the redirect URI
     * @return true if the redirect URI is authorized, false otherwise
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return redirectUriConfiguration.getAuthorizedRedirectUris().stream().anyMatch(
                authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return clientRedirectUri.equals(authorizedURI);
                });
    }

    /**
     * Get the OAuth2 user
     * 
     * @param authentication the authentication
     * @return the OAuth2 user
     */
    private OAuth2User getOAuth2User(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            return (OAuth2User) principal;
        }
        return null;
    }

    /**
     * Clear the authentication attributes
     * 
     * @param request  the request
     * @param response the response
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
