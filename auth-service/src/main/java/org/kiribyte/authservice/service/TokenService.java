package org.kiribyte.authservice.service;

import org.kiribyte.dto.UserWithRolesDto;

import java.util.Date;

public interface TokenService {

    String generateAccessToken(UserWithRolesDto user);

    String generateRefreshToken(UserWithRolesDto user);

    boolean validateToken(String token);

    Long getUserId(String token);

    Date getExpirationDate(String token);
}
