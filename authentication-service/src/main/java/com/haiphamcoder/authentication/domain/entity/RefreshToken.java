package com.haiphamcoder.authentication.domain.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiphamcoder.authentication.domain.model.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static RefreshToken clone(RefreshToken token) {
        return RefreshToken.builder()
                .id(token.getId())
                .tokenValue(token.getTokenValue())
                .tokenType(token.getTokenType())
                .expiredAt(token.getExpiredAt())
                .userId(token.getUserId())
                .createdAt(token.getCreatedAt())
                .build();
    }
}
