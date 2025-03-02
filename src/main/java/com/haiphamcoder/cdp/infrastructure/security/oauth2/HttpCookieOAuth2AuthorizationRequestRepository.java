package com.haiphamcoder.cdp.infrastructure.security.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.shared.CookieUtils;
import com.haiphamcoder.cdp.shared.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final HttpSessionOAuth2AuthorizationRequestRepository httpSessionOAuth2AuthorizationRequestRepository;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return httpSessionOAuth2AuthorizationRequestRepository.loadAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
            HttpServletResponse response) {
        httpSessionOAuth2AuthorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request,
                response);
        saveParamInCookie(request, response, OAuth2AuthorizationRequestParams.REDIRECT_URI.getValue());
    }

    private void saveParamInCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        String value = request.getParameter(name);
        log.info("Request param " + name + " is " + value);
        if (StringUtils.isNotBlank(value)) {
            CookieUtils.addCookie(response, name, value);
        } else {
            throw new IllegalArgumentException("Request param " + name + " is required and cannot be empty");
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
            HttpServletResponse response) {
        return httpSessionOAuth2AuthorizationRequestRepository.removeAuthorizationRequest(request, response);
    }

}
