package org.kiribyte.authservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiribyte.authservice.dto.RefreshTokenRequest;
import org.kiribyte.authservice.dto.TokensDto;
import org.kiribyte.authservice.model.TokenEntity;
import org.kiribyte.authservice.repository.TokenRepository;
import org.kiribyte.authservice.service.TokenService;
import org.kiribyte.authservice.service.UserClient;
import org.kiribyte.authservice.service.impl.AuthServiceImpl;
import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private UserClient userClient;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    private AuthServiceImpl authService;
    private UserWithRolesDto testUser;
    private UserDto createdUser;
    private UserLoginDto userLoginDto;
    private UserRegisterDto userRegisterDto;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userClient, tokenService, tokenRepository);

        testUser = new UserWithRolesDto();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRoles(Set.of("ROLE_USER"));

        userLoginDto = new UserLoginDto("test@example.com", "password");
        userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setPassword("password");
    }

    @Test
    void login_ValidCredentials_ReturnsTokens() {
        // Arrange
        when(userClient.verifyCredentials(userLoginDto)).thenReturn(testUser);
        when(tokenService.generateAccessToken(testUser)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(tokenService.getExpirationDate("refresh-token")).thenReturn(new Date());
        when(tokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TokensDto tokens = authService.login(userLoginDto);

        // Assert
        assertNotNull(tokens);
        assertEquals("access-token", tokens.getAccessToken());
        assertEquals("refresh-token", tokens.getRefreshToken());
        verify(userClient).verifyCredentials(userLoginDto);
        verify(tokenRepository).save(any(TokenEntity.class));
    }

    @Test
    void register_ValidUser_CreatesUserAndReturnsTokens() {
        // Arrange
        when(userClient.createUser(userRegisterDto)).thenReturn(createdUser);
        when(userClient.verifyCredentials(any(UserLoginDto.class))).thenReturn(testUser);
        when(tokenService.generateAccessToken(testUser)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(tokenService.getExpirationDate("refresh-token")).thenReturn(new Date());
        when(tokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TokensDto tokens = authService.register(userRegisterDto);

        // Assert
        assertNotNull(tokens);
        verify(userClient).createUser(userRegisterDto);
        verify(userClient).verifyCredentials(any(UserLoginDto.class));
        verify(tokenRepository).save(any(TokenEntity.class));
    }

    @Test
    void logout_ValidToken_DeletesToken() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        when(tokenService.validateToken("valid-refresh-token")).thenReturn(true);
        when(tokenService.getUserId("valid-refresh-token")).thenReturn(1L);

        TokenEntity tokenEntity = new TokenEntity();
        when(tokenRepository.findByUserId(1L)).thenReturn(Optional.of(tokenEntity));

        // Act
        authService.logout(request);

        // Assert
        verify(tokenService).validateToken("valid-refresh-token");
        verify(tokenService).getUserId("valid-refresh-token");
        verify(tokenRepository).findByUserId(1L);
        verify(tokenRepository).delete(tokenEntity);
    }
}
