package com.haiphamcoder.cdp.infrastructure.security.oauth2.user;

import java.util.Map;

import com.haiphamcoder.cdp.infrastructure.security.oauth2.OAuth2Provider;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
            String accessToken,
            Map<String, Object> attributes) {
        if (OAuth2Provider.GOOGLE.getRegistrationId().equals(registrationId)) {
            return new GoogleOAuth2UserInfo(accessToken, attributes);
        } else {
            throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        }
    }
}
