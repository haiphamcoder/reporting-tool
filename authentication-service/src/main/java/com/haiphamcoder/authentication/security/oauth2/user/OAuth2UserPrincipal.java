package com.haiphamcoder.authentication.security.oauth2.user;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.haiphamcoder.authentication.domain.model.Role;

public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private final OAuth2UserInfo oAuth2UserInfo;

    public OAuth2UserPrincipal(OAuth2UserInfo oAuth2UserInfo) {
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Role.fromName("user").getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getName();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return oAuth2UserInfo.getEmail();
    }

}
