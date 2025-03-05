package com.haiphamcoder.cdp.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.AuthenticationResponse;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.domain.model.Role;
import com.haiphamcoder.cdp.domain.model.TokenType;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtTokenProvider;
import com.haiphamcoder.cdp.shared.DateTimeUtils;
import com.haiphamcoder.cdp.shared.SnowflakeIdGenerator;
import com.haiphamcoder.cdp.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
        private final UserService userService;
        private final RefreshTokenService refreshTokenService;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

        public User register(RegisterRequest request) {
                User user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .username(request.getUsername())
                                .password(request.getPassword())
                                .email(request.getEmail())
                                .role(request.getRole())
                                .build();
                return userService.saveUser(user);
        }

        public boolean checkUsernameExisted(String username) {
                return userService.getUserByUsername(username) != null;
        }

        public boolean checkEmailExisted(String email) {
                return userService.getUserByEmail(email) != null;
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(),
                                                        request.getPassword()));
                        User authenticatedUser = (User) authentication.getPrincipal();
                        String refreshToken = jwtTokenProvider.generateRefreshToken(authenticatedUser.getUsername());
                        RefreshToken refreshTokenEntity = RefreshToken.builder()
                                        .id(snowflakeIdGenerator.generateId())
                                        .user(authenticatedUser)
                                        .tokenValue(refreshToken)
                                        .tokenType(TokenType.BEARER)
                                        .expiredAt(DateTimeUtils.convertToLocalDateTime(
                                                        jwtTokenProvider.extractExpiration(refreshToken)))
                                        .build();
                        RefreshToken savedRefreshToken = refreshTokenService.saveUserToken(refreshTokenEntity);
                        if (savedRefreshToken == null) {
                                return AuthenticationResponse.builder()
                                                .errorMessage("Failed to generate refresh token")
                                                .status(CommonConstants.AUTHEN_FAILED)
                                                .build();
                        }
                        String accessTokenValue = jwtTokenProvider.generateAccessToken(authenticatedUser.getUsername());
                        return AuthenticationResponse.builder()
                                        .userId(authenticatedUser.getId())
                                        .accessToken(accessTokenValue)
                                        .refreshToken(savedRefreshToken.getTokenValue())
                                        .status(CommonConstants.AUTHEN_SUCCESS)
                                        .build();

                } catch (AuthenticationException e) {
                        return AuthenticationResponse.builder()
                                        .errorMessage("Invalid username or password")
                                        .status(CommonConstants.AUTHEN_FAILED)
                                        .build();
                }
        }

        public String refreshToken(String authHeader) {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String refreshToken = authHeader.substring(7);
                        RefreshToken existedRefreshToken = refreshTokenService.getTokenByValue(refreshToken);
                        if (existedRefreshToken != null) {
                                String accessToken = jwtTokenProvider
                                                .generateAccessToken(existedRefreshToken.getUser());
                                if (!StringUtils.isNullOrEmpty(accessToken)) {
                                        return accessToken;
                                }
                        }
                }
                return null;
        }

        public boolean createAdminUser() {
                RegisterRequest request = RegisterRequest.builder()
                                .firstName("Admin")
                                .lastName("")
                                .username("admin")
                                .password("admin")
                                .email("admin@example.com")
                                .role(Role.ADMIN)
                                .build();
                return register(request) != null;
        }
}
