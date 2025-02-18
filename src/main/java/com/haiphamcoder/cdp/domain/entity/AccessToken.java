package com.haiphamcoder.cdp.domain.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiphamcoder.cdp.domain.model.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "access_tokens")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

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

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id", nullable = false)
    private RefreshToken refreshToken;

    public AccessToken(AccessToken other){
        this.id = other.id;
        this.tokenValue = other.tokenValue;
        this.tokenType = other.tokenType;
        this.expiredAt = other.expiredAt;
        this.createdAt = other.createdAt;
        this.refreshToken = other.refreshToken;
    }
}
