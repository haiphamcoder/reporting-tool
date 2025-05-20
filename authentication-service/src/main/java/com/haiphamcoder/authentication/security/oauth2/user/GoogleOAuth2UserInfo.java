package com.haiphamcoder.authentication.security.oauth2.user;

import java.util.Map;

import com.haiphamcoder.authentication.security.oauth2.OAuth2Provider;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final String accessToken;

    private final Map<String, Object> attributes;

    private final String id;

    private final String email;

    private final String name;

    private final String firstName;

    private final String lastName;

    private final String nickname;

    private final String profileImageUrl;

    /**
     * Constructor for GoogleOAuth2UserInfo
     * 
     * @param accessToken access token from Google
     * @param attributes  attributes from Google
     */
    public GoogleOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.accessToken = accessToken;
        this.attributes = attributes;
        this.id = String.valueOf(attributes.get("sub"));
        this.email = (String) attributes.get("email");
        this.name = (String) attributes.get("name");
        this.firstName = (String) attributes.get("given_name");
        this.lastName = (String) attributes.get("family_name");
        this.nickname = (String) attributes.get("nickname");
        this.profileImageUrl = (String) attributes.get("picture");
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GOOGLE;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

}
