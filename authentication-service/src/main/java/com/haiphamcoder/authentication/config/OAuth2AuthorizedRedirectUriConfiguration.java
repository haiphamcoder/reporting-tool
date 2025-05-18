package com.haiphamcoder.authentication.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2AuthorizedRedirectUriConfiguration {

    @Value("${app.oauth2.authorized.redirect-uris}")
    private String authorizedRedirectUris;

    public List<String> getAuthorizedRedirectUris() {
        return Arrays.asList(authorizedRedirectUris.split(","));
    }
}
