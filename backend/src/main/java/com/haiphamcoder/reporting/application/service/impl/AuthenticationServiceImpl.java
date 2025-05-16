package com.haiphamcoder.reporting.application.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.application.service.AuthenticationService;
import com.haiphamcoder.reporting.application.service.RefreshTokenService;
import com.haiphamcoder.reporting.application.service.UserService;
import com.haiphamcoder.reporting.domain.entity.RefreshToken;
import com.haiphamcoder.reporting.domain.entity.User;
import com.haiphamcoder.reporting.domain.model.AuthenticationRequest;
import com.haiphamcoder.reporting.domain.model.RegisterRequest;
import com.haiphamcoder.reporting.domain.model.Role;
import com.haiphamcoder.reporting.domain.model.TokenType;
import com.haiphamcoder.reporting.infrastructure.security.jwt.JwtTokenProvider;
import com.haiphamcoder.reporting.shared.CookieUtils;
import com.haiphamcoder.reporting.shared.DateTimeUtils;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;
import com.haiphamcoder.reporting.shared.security.exception.UsernameOrPasswordIncorrectException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
        private final UserService userService;
        private final RefreshTokenService refreshTokenService;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

        private static final String COOKIE_USER_ID = "user-id";
        private static final String COOKIE_ACCESS_TOKEN = "access-token";
        private static final String COOKIE_REFRESH_TOKEN = "refresh-token";

        @Override
        public User register(RegisterRequest request) {
                User user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .username(request.getUsername())
                                .password(request.getPassword())
                                .email(request.getEmail())
                                .role(request.getRole().getName())
                                .build();
                return userService.saveUser(user);
        }

        @Override
        public boolean checkUsernameExisted(String username) {
                return userService.getUserByUsername(username) != null;
        }

        @Override
        public boolean checkEmailExisted(String email) {
                return userService.getUserByEmail(email) != null;
        }

        @Override
        public RestAPIResponse<String> authenticate(AuthenticationRequest request, HttpServletResponse response) {
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
                                throw new RuntimeException("Failed to save refresh token");
                        }
                        String accessTokenValue = jwtTokenProvider.generateAccessToken(authenticatedUser.getUsername());

                        CookieUtils.addCookie(response, COOKIE_USER_ID, String.valueOf(authenticatedUser.getId()));
                        CookieUtils.addCookie(response, COOKIE_ACCESS_TOKEN, accessTokenValue);
                        CookieUtils.addCookie(response, COOKIE_REFRESH_TOKEN, savedRefreshToken.getTokenValue());

                        return RestAPIResponse.ResponseFactory.createResponse("Authentication successful");

                } catch (AuthenticationException e) {
                        throw new UsernameOrPasswordIncorrectException();
                }
        }

        @Override
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

        @Override
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
