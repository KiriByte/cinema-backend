package org.kiribyte.authservice.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.lang.NonNull;
import jakarta.annotation.PostConstruct;
import org.kiribyte.authservice.exception.TokenExpiredException;
import org.kiribyte.authservice.exception.TokenValidationException;
import org.kiribyte.authservice.service.TokenService;
import org.kiribyte.dto.UserWithRolesDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-expiration-minutes}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String generateAccessToken(@NonNull UserWithRolesDto user) {
        var expirationAt = Date.from(Instant.now().plus(accessTokenExpiration, ChronoUnit.MINUTES));
        return createTokenBuilder(user, expirationAt)
                .claim("tokenType", "accessToken")
                .compact();
    }

    @Override
    public String generateRefreshToken(@NonNull UserWithRolesDto user) {
        var expirationAt = Date.from(Instant.now().plus(refreshTokenExpiration, ChronoUnit.DAYS));
        return createTokenBuilder(user, expirationAt)
                .claim("tokenType", "refreshToken")
                .compact();
    }

    private JwtBuilder createTokenBuilder(UserWithRolesDto user, Date expiration) {

        String fullname = user.getFirstName() + " " + user.getLastName();
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(issuer)
                .expiration(expiration)
                .signWith(secretKey)
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles())
                .claim("name", fullname);
    }

    private Claims decodeToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token has expired");
        } catch (MalformedJwtException | SecurityException e) {
            throw new TokenValidationException("Invalid JWT token format or signature");
        } catch (IllegalArgumentException e) {
            throw new TokenValidationException("Token is null or empty");
        } catch (Exception e) {
            throw new TokenValidationException("Invalid JWT token: " + e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (issuer != null && !issuer.equals(claims.getIssuer())) {
                return false;
            }
            if (claims.getNotBefore() != null && claims.getNotBefore().after(new Date())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {

        String subject = decodeToken(token).getSubject();
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new TokenValidationException("Subject claim is not a valid Long ID");
        }
    }

    public String getEmailFromToken(String token) {
        try {
            return decodeToken(token).get("email", String.class);
        } catch (Exception e) {
            throw new TokenValidationException("Failed to extract email from token");
        }
    }

    public Set<String> getRolesFromToken(String token) {
        try {
            Claims claims = decodeToken(token);
            @SuppressWarnings("unchecked")
            List<String> rolesList = claims.get("roles", List.class);
            if (rolesList == null) {
                return Collections.emptySet();
            }
            return new HashSet<>(rolesList);
        } catch (Exception e) {
            throw new TokenValidationException("Failed to extract roles from token");
        }
    }


    public String getTokenType(String token) {
        return decodeToken(token).get("tokenType", String.class);
    }

    @Override
    public Date getExpirationDate(String token) {
        try {
            return decodeToken(token).getExpiration();
        } catch (Exception e) {
            throw new TokenValidationException("Failed to extract expiration date from token");
        }
    }

}
