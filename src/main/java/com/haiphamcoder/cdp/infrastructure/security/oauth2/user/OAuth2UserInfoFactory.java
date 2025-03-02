package com.haiphamcoder.cdp.infrastructure.security.oauth2.user;

import java.util.Map;

import com.haiphamcoder.cdp.infrastructure.security.oauth2.OAuth2Provider;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(OAuth2Provider.GOOGLE.getValue())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        }
    }
}
