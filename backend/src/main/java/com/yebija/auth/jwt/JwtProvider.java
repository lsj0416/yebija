package com.yebija.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(
                        jwtProperties.getSecret().getBytes()
                )
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long churchId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(churchId))
                .claim("email", email)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getAccessExpiration()))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long churchId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(churchId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getRefreshExpiration()))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getChurchId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public boolean isExpired(String token) {
        try {
            parseToken(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
