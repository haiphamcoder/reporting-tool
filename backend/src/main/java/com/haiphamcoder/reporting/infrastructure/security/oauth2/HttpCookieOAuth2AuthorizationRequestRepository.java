package com.haiphamcoder.reporting.infrastructure.security.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import com.haiphamcoder.reporting.infrastructure.config.CommonConstants;
import com.haiphamcoder.reporting.shared.CookieUtils;
import com.haiphamcoder.reporting.shared.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /**
     * Load the authorization request from the cookies
     * 
     * @param request the request
     * @return the authorization request
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, CommonConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * Save the authorization request to the cookies
     * 
     * @param authorizationRequest the authorization request
     * @param request              the request
     * @param response             the response
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
            HttpServletResponse response) {

        // If the authorization request is null, delete the cookies
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, CommonConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }

        // Add the authorization request to the cookies
        CookieUtils.addCookie(response,
                CommonConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest),
                CommonConstants.COOKIE_EXPIRE_SECONDS);

        // Add the redirect URI to the cookies
        String redirectUriAfterLogin = request.getParameter(CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME);
        if (!StringUtils.isNullOrEmpty(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response,
                    CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    CommonConstants.COOKIE_EXPIRE_SECONDS);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
            HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    /**
     * Remove the authorization request cookies
     * 
     * @param request  the request
     * @param response the response
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, CommonConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, CommonConstants.REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
