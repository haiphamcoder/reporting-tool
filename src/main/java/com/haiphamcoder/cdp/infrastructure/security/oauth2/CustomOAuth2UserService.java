package com.haiphamcoder.cdp.infrastructure.security.oauth2;

import java.util.UUID;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.repository.UserRepository;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.exception.OAuth2AuthenticationProcessingException;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.user.OAuth2UserInfo;
import com.haiphamcoder.cdp.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import com.haiphamcoder.cdp.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        log.info("OAuth2 user request: {}", userRequest);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 user: {}", oAuth2User);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (StringUtils.isNullOrEmpty(oAuth2UserInfo.getEmail())){
            throw new IllegalArgumentException("Email not found from OAuth2 provider");
        }

        User user = userRepository.getUserByEmail(oAuth2UserInfo.getEmail()).orElse(null);
        if (user != null) {
            if (!user.getProvider().equals(OAuth2Provider.fromName(userRequest.getClientRegistration().getRegistrationId().toLowerCase()).getName())){
                throw new OAuth2AuthenticationProcessingException("error","Looks like you're signed up with " + user.getProvider() + " account. Please use your " + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }
        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getName());
        existingUser.setAvatarUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.saveUser(existingUser);
        
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                        .provider(OAuth2Provider.fromName(userRequest.getClientRegistration().getRegistrationId().toLowerCase()).getName())
                        .providerId(oAuth2UserInfo.getId())
                        .username(oAuth2UserInfo.getEmail())
                        .email(oAuth2UserInfo.getEmail())
                        .firstName(oAuth2UserInfo.getFirstName())
                        .lastName(oAuth2UserInfo.getLastName())
                        .avatarUrl(oAuth2UserInfo.getImageUrl())
                        .emailVerified(true)
                        .password(UUID.randomUUID().toString())
                        .build();
        return userRepository.saveUser(user);
    }
    
}
