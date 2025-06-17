package com.haiphamcoder.authentication.security.oauth2.user;

import java.util.Map;

import com.haiphamcoder.authentication.security.oauth2.OAuth2Provider;

/**
 * OAuth2UserInfo interface
 */
public interface OAuth2UserInfo {

    OAuth2Provider getProvider();

    String getAccessToken();

    Map<String, Object> getAttributes();

    String getId();

    String getEmail();

    String getName();

    String getFirstName();

    String getLastName();

    String getNickname();

    String getProfileImageUrl();

}
