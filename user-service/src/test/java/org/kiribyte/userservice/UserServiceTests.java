package org.kiribyte.userservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.userservice.entity.Role;
import org.kiribyte.userservice.entity.User;
import org.kiribyte.userservice.exception.UserAlreadyExistsException;
import org.kiribyte.userservice.exception.UserNotFoundException;
import org.kiribyte.userservice.mapper.UserMapper;
import org.kiribyte.userservice.repostory.RoleRepository;
import org.kiribyte.userservice.repostory.UserRepository;
import org.kiribyte.userservice.service.impl.UserServiceImpl;
import org.kiribyte.userservice.util.PasswordUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDto() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createUser_WhenEmailNotExists_ShouldCreateUser() {
        // Arrange
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail("newuser@example.com");
        registerDto.setPassword("password123");
        registerDto.setConfirmPassword("password123");

        User user = new User();
        user.setEmail("newuser@example.com");
        user.setFirstName("Name");
        user.setLastName("LastName");
        user.setRoles(new HashSet<>());

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setRoles(new HashSet<>());

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(1L);
        expectedUserDto.setEmail("newuser@example.com");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setUsers(new HashSet<>());

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userMapper.toUserFromRegisterDto(registerDto)).thenReturn(user);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.createUser(registerDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser@example.com", result.getEmail());
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail("existing@example.com");
        registerDto.setPassword("password123");
        registerDto.setConfirmPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(registerDto);
        });
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyCredentials_WhenValidCredentials_ShouldReturnUserWithRoles() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("correctPassword");

        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("ROLE_USER");
        role.setUsers(new HashSet<>());
        roles.add(role);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("Name");
        user.setLastName("LastName");
        user.setPassword(PasswordUtil.hashPassword("correctPassword"));
        user.setRoles(roles);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        var result = userService.verifyCredentials(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user@example.com", result.getEmail());
        assertEquals("Name", result.getFirstName());
        assertEquals("LastName", result.getLastName());
        assertTrue(result.getRoles().contains("ROLE_USER"));
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUserDto() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(1L);
        expectedUserDto.setEmail(email);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email.toLowerCase());
    }
}

