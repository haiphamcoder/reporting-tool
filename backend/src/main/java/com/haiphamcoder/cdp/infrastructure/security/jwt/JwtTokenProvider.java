package com.haiphamcoder.cdp.infrastructure.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.haiphamcoder.cdp.domain.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {

    private final String jwtSecret;
    private final long jwtExpiration;
    private final long jwtRefreshExpiration;

    public JwtTokenProvider(@Value("${security.jwt.secret-key}") String jwtSecret,
            @Value("${security.jwt.expiration}") long jwtExpiration,
            @Value("${security.jwt.refresh-token.expiration}") long jwtRefreshExpiration) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new SignatureException("Invalid JWT signature");
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    public String generateAccessToken(String username) {
        return buildToken(new HashMap<>(), username, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtRefreshExpiration);
    }

    public String generateRefreshToken(String username) {
        return buildToken(new HashMap<>(), username, jwtRefreshExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public Map<String, String> generateTokens(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String accessToken = buildToken(new HashMap<>(), user.getUsername(), jwtExpiration);
        String refreshToken = buildToken(new HashMap<>(), user.getUsername(), jwtRefreshExpiration);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access-token", accessToken);
        tokens.put("refresh-token", refreshToken);
        return tokens;
    }

    private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact(); 
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return buildToken(extraClaims, userDetails.getUsername(), expiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
