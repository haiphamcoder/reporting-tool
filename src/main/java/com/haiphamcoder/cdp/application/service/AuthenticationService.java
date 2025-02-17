package com.haiphamcoder.cdp.application.service;

import java.util.Base64;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.AccessToken;
import com.haiphamcoder.cdp.domain.entity.RefreshToken;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.AuthenticationResponse;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.domain.model.Role;
import com.haiphamcoder.cdp.domain.model.TokenType;
import com.haiphamcoder.cdp.infrastructure.security.jwt.JwtTokenProvider;
import com.haiphamcoder.cdp.shared.DateTimeUtils;
import com.haiphamcoder.cdp.shared.SnowflakeIdGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
        private final UserService userService;
        private final AccessTokenService accessTokenService;
        private final RefreshTokenService refreshTokenService;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

        public boolean register(RegisterRequest request) {
                User user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .username(request.getUsername())
                                .password(request.getPassword())
                                .email(request.getEmail())
                                .role(request.getRole())
                                .build();
                User createdUser = userService.createUser(user);
                deleteAllUserTokens(createdUser);
                return createdUser != null;
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));
                User existedUser = userService.getUserByUsername(request.getUsername());
                if (existedUser != null) {
                        String refreshToken = jwtTokenProvider.generateRefreshToken(existedUser);
                        RefreshToken refreshTokenEntity = RefreshToken.builder()
                                        .id(snowflakeIdGenerator.generateId())
                                        .user(existedUser)
                                        .tokenValue(refreshToken)
                                        .tokenType(TokenType.BEARER)
                                        .expiredAt(DateTimeUtils.convertToLocalDateTime(
                                                        jwtTokenProvider.extractExpiration(refreshToken)))
                                        .build();
                        RefreshToken savedRefreshToken = refreshTokenService.saveUserToken(refreshTokenEntity);
                        if (savedRefreshToken == null) {
                                return null;
                        }

                        String accessToken = jwtTokenProvider.generateAccessToken(existedUser);
                        AccessToken accessTokenEntity = AccessToken.builder()
                                        .id(snowflakeIdGenerator.generateId())
                                        .refreshToken(savedRefreshToken)
                                        .tokenValue(accessToken)
                                        .tokenType(TokenType.BEARER)
                                        .expiredAt(DateTimeUtils.convertToLocalDateTime(
                                                        jwtTokenProvider.extractExpiration(accessToken)))
                                        .build();
                        AccessToken savedAccessToken = accessTokenService.saveUserToken(accessTokenEntity);
                        if (savedAccessToken == null) {
                                return null;
                        }

                        return AuthenticationResponse.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .build();
                }
                return null;
        }

        private void deleteAllUserTokens(User user) {
                List<RefreshToken> refreshTokens = refreshTokenService.getAllValidTokens(user.getId());
                refreshTokens.forEach(refreshToken -> refreshTokenService.deleteToken(refreshToken.getId()));
        }

        public String refreshToken(String authHeader) {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String refreshToken = authHeader.substring(7);
                        String username = jwtTokenProvider.extractUsername(refreshToken);
                        RefreshToken existedRefreshToken = refreshTokenService.getTokenByValue(refreshToken);
                        if (existedRefreshToken != null
                                        && existedRefreshToken.getUser().getUsername().equals(username)) {
                                String accessToken = jwtTokenProvider.generateAccessToken(existedRefreshToken.getUser());
                                AccessToken accessTokenEntity = AccessToken.builder()
                                                .id(snowflakeIdGenerator.generateId())
                                                .refreshToken(existedRefreshToken)
                                                .tokenValue(accessToken)
                                                .tokenType(TokenType.BEARER)
                                                .expiredAt(DateTimeUtils.convertToLocalDateTime(
                                                                jwtTokenProvider.extractExpiration(accessToken)))
                                                .build();
                                AccessToken savedAccessToken = accessTokenService.saveUserToken(accessTokenEntity);
                                if (savedAccessToken != null) {
                                        return savedAccessToken.getTokenValue();
                                }
                        }
                }
                return null;
        }

        public boolean createAdminUser() {
                RegisterRequest request = RegisterRequest.builder()
                                .firstName("Admin")
                                .lastName("Admin")
                                .username("admin")
                                .password("admin")
                                .email("admin@email.com")
                                .role(Role.ADMIN)
                                .build();
                return register(request);
        }
}
