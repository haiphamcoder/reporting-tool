package com.haiphamcoder.authentication.security.oauth2;

import java.util.UUID;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.security.oauth2.exception.OAuth2AuthenticationProcessingException;
import com.haiphamcoder.authentication.security.oauth2.user.OAuth2UserInfo;
import com.haiphamcoder.authentication.security.oauth2.user.OAuth2UserInfoFactory;
import com.haiphamcoder.authentication.security.oauth2.user.OAuth2UserPrincipal;
import com.haiphamcoder.authentication.service.UserGrpcClient;
import com.haiphamcoder.authentication.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserGrpcClient userGrpcClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            log.error("Error in CustomOAuth2UserService.loadUser", e);
            throw e;
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, accessToken,
                oAuth2User.getAttributes());
        log.info("OAuth2 user info: {}", oAuth2UserInfo);
        
        if (StringUtils.isNullOrEmpty(oAuth2UserInfo.getEmail())) {
            log.error("Email not found from OAuth2 provider");
            throw new IllegalArgumentException("Email not found from OAuth2 provider");
        }

        UserDto user = userGrpcClient.getUserByEmail(oAuth2UserInfo.getEmail());
        log.info("Existing user found: {}", user);
        
        if (user != null) {
            if (!registrationId.equals(user.getProvider())) {
                log.error("User signed up with different provider: {}", user.getProvider());
                throw new OAuth2AuthenticationProcessingException("error",
                        "Looks like you're signed up with " + user.getProvider() + " account. Please use your "
                                + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
            log.info("Updated existing user: {}", user);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
            log.info("Registered new user: {}", user);
        }
        return new OAuth2UserPrincipal(oAuth2UserInfo);
    }

    private UserDto updateExistingUser(UserDto existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setProviderId(oAuth2UserInfo.getId());
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setAvatarUrl(oAuth2UserInfo.getProfileImageUrl());
        existingUser.setEmailVerified(true);
        return userRepository.saveUser(existingUser);
    }

    private UserDto registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        UserDto user = UserDto.builder()
                .provider(userRequest.getClientRegistration().getRegistrationId())
                .providerId(oAuth2UserInfo.getId())
                .username(oAuth2UserInfo.getEmail())
                .email(oAuth2UserInfo.getEmail())
                .firstName(oAuth2UserInfo.getFirstName())
                .lastName(oAuth2UserInfo.getLastName())
                .avatarUrl(oAuth2UserInfo.getProfileImageUrl())
                .emailVerified(true)
                .password(UUID.randomUUID().toString())
                .build();
        return userRepository.saveUser(user);
    }

}
