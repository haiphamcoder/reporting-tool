package com.haiphamcoder.reporting.domain.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiphamcoder.reporting.domain.model.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshToken {

    @Id
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "type", nullable = false)
    private TokenType tokenType = TokenType.BEARER;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static RefreshToken clone(RefreshToken token) {
        return RefreshToken.builder()
                .id(token.getId())
                .tokenValue(token.getTokenValue())
                .tokenType(token.getTokenType())
                .expiredAt(token.getExpiredAt())
                .user(token.getUser())
                .build();
    }
}
