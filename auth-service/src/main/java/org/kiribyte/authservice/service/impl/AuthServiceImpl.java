package org.kiribyte.authservice.service.impl;

import org.kiribyte.authservice.dto.RefreshTokenRequest;
import org.kiribyte.authservice.dto.TokensDto;
import org.kiribyte.authservice.exception.TokenValidationException;
import org.kiribyte.authservice.model.TokenEntity;
import org.kiribyte.authservice.repository.TokenRepository;
import org.kiribyte.authservice.service.TokenService;
import org.kiribyte.authservice.service.UserClient;
import org.kiribyte.authservice.util.TokenHash;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    private final UserClient userClient;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;

    public AuthServiceImpl(UserClient userClient, TokenService tokenService, TokenRepository tokenRepository) {
        this.userClient = userClient;
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
    }

    public TokensDto login(UserLoginDto userLoginDto) {
        UserWithRolesDto userWithRolesDto = userClient.verifyCredentials(userLoginDto);
        TokensDto tokens = getTokens(userWithRolesDto);
        /// ///////////////////////////
        String refreshToken = tokens.getRefreshToken();
        TokenEntity tokenEntity = tokenRepository.findByUserId(userWithRolesDto.getId())
                .orElse(new TokenEntity());
        tokenEntity.setUserId(userWithRolesDto.getId());
        tokenEntity.setToken(TokenHash.hashToken(refreshToken));
        tokenEntity.setExpiredAt(tokenService.getExpirationDate(refreshToken));
        tokenRepository.save(tokenEntity);
        return tokens;
    }

    public TokensDto register(UserRegisterDto userRegisterDto) {
        userClient.createUser(userRegisterDto);
        var userLoginDto = new UserLoginDto(userRegisterDto.getEmail(), userRegisterDto.getPassword());
        return login(userLoginDto);
    }

    public void logout(RefreshTokenRequest refreshTokenRequest) {
        var refreshToken = refreshTokenRequest.getRefreshToken();
        if (!tokenService.validateToken(refreshToken)) {
            throw new TokenValidationException("Invalid token");
        }
        Long userId = tokenService.getUserId(refreshToken);
        var tokenFromRepository = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new TokenValidationException("Token not found"));

        tokenRepository.delete(tokenFromRepository);

    }

    public TokensDto refresh(RefreshTokenRequest refreshTokenRequest) {

        var refreshToken = refreshTokenRequest.getRefreshToken();

        var userId = tokenService.getUserId(refreshToken);
        TokenEntity tokenFromRepository = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new TokenValidationException("Token not found"));

        if (!TokenHash.verifyToken(refreshToken, tokenFromRepository.getToken())) {
            throw new TokenValidationException("Invalid token");
        }

        if (!tokenService.validateToken(refreshToken)) {
            throw new TokenValidationException("Invalid token");
        }

        UserWithRolesDto userById = userClient.getUserWithRolesById(userId);
        TokensDto tokens = getTokens(userById);

        tokenFromRepository.setToken(TokenHash.hashToken(tokens.getRefreshToken()));
        tokenFromRepository.setExpiredAt(tokenService.getExpirationDate(tokens.getRefreshToken()));
        tokenRepository.save(tokenFromRepository);

        return tokens;
    }

    private TokensDto getTokens(UserWithRolesDto userWithRolesDto) {
        var access = tokenService.generateAccessToken(userWithRolesDto);
        var refresh = tokenService.generateRefreshToken(userWithRolesDto);
        return new TokensDto(access, refresh);
    }


}
