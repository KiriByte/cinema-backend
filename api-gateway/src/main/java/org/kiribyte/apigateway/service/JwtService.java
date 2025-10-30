package org.kiribyte.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.kiribyte.apigateway.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims decodeToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Token has expired");
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!issuer.equals(claims.getIssuer())) {
                throw new InvalidTokenException("Invalid token issuer");
            }

            if (claims.getExpiration().before(new Date())) {
                throw new InvalidTokenException("Token has expired");
            }

            return true;
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token has expired");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Malformed token");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid token signature");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    public List<String> getRolesFromToken(String token) {

        Claims claims = decodeToken(token);
        List roles = claims.get("roles", List.class);
        if (roles == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(roles);
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }
}
