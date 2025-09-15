package org.kiribyte.authservice.service.impl;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.kiribyte.authservice.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenServiceImpl {

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @Value("${jwt.access-token-expiration-minutes}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }


//    public String generateAccessToken(@NonNull User user) {
//        final Instant accessExpirationInstant =
//                Instant.now()
//                        .plus(accessTokenExpiration, ChronoUnit.MINUTES)
//                        .atZone(ZoneId.systemDefault())
//                        .toInstant();
//        final Date accessExpiration = Date.from(accessExpirationInstant);
//        return Jwts.builder()
//                .subject(user.getEmail())
//                .expiration(accessExpiration)
//                .claim("roles", user.getRoles())
//                .claim("firstName", user.getFirstName())
//                .claim("lastName", user.getLastName())
//                .claim("tokenType", "accessToken")
//                .signWith(secretKey)
//                .compact();
//    }

    public String generateAccessToken(@NonNull User user) {
        var expirationAt = calculateExpirationInMinutes(accessTokenExpiration);
        return createTokenBuilder(user, expirationAt)
                .claim("tokenType", "accessToken")
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        var expirationAt = calculateExpirationInDays(refreshTokenExpiration);
        return createTokenBuilder(user, expirationAt)
                .claim("tokenType", "refreshToken")
                .compact();
    }

    private JwtBuilder createTokenBuilder(User user, Date expiration) {
        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(expiration)
                .signWith(secretKey)
                .claim("roles", user.getRoles())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName());
    }

    private Date calculateExpirationInMinutes(long minutes) {
        return Date.from(Instant.now().plus(minutes, ChronoUnit.MINUTES));
    }

    private Date calculateExpirationInDays(long days) {
        return Date.from(Instant.now().plus(days, ChronoUnit.DAYS));
    }
}
