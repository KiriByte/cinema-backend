package org.kiribyte.authservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiribyte.authservice.service.impl.TokenServiceImpl;
import org.kiribyte.dto.UserWithRolesDto;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTests {

    private TokenServiceImpl tokenService;
    private UserWithRolesDto testUser;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();

        ReflectionTestUtils.setField(tokenService, "secret", "testSecretKeytestSecretKeytestSecretKeytestSecretKeytestSecretKeytestSecretKeytestSecretKey");
        ReflectionTestUtils.setField(tokenService, "issuer", "test-issuer");
        ReflectionTestUtils.setField(tokenService, "accessTokenExpiration", 30L);
        ReflectionTestUtils.setField(tokenService, "refreshTokenExpiration", 7L);

        tokenService.init();

        testUser = new UserWithRolesDto();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN"));
    }

    @Test
    void generateAccessToken_ValidUser_ReturnsValidToken() {
        // Arrange
        // testUser уже настроен в setUp()

        // Act
        String token = tokenService.generateAccessToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(tokenService.validateToken(token));
    }

    @Test
    void getUserId_ValidToken_ReturnsUserId() {
        // Arrange
        String token = tokenService.generateAccessToken(testUser);

        // Act
        Long userId = tokenService.getUserId(token);

        // Assert
        assertEquals(testUser.getId(), userId);
    }

    @Test
    void getRolesFromToken_ValidToken_ReturnsUserRoles() {
        // Arrange
        String token = tokenService.generateAccessToken(testUser);

        // Act
        Set<String> roles = tokenService.getRolesFromToken(token);

        // Assert
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }
}
