package org.kiribyte.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.kiribyte.userservice.entity.Role;
import org.kiribyte.userservice.entity.User;
import org.kiribyte.userservice.exception.InvalidCredentialsException;
import org.kiribyte.userservice.exception.PasswordMismatchException;
import org.kiribyte.userservice.exception.UserAlreadyExistsException;
import org.kiribyte.userservice.exception.UserNotFoundException;
import org.kiribyte.userservice.mapper.UserMapper;
import org.kiribyte.userservice.repostory.RoleRepository;
import org.kiribyte.userservice.repostory.UserRepository;
import org.kiribyte.userservice.service.impl.UserServiceImpl;
import org.kiribyte.userservice.util.PasswordUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, roleRepository, userMapper);
    }

    @Test
    void createUser_success_assignsDefaultRole_andHashesPassword_lowercasesEmail() {
        UserRegisterDto registerDto = new UserRegisterDto("Test@Email.com", "password", "password");
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);

        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));

        User mapped = new User();
        mapped.setEmail("test@email.com");
        when(userMapper.toUserFromRegisterDto(registerDto)).thenReturn(mapped);

        User saved = new User();
        saved.setId(10L);
        saved.setEmail("test@email.com");
        saved.setFirstName(null);
        saved.setLastName(null);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto expectedDto = new UserDto(10L, "test@email.com", null, null);
        when(userMapper.toUserDto(saved)).thenReturn(expectedDto);

        UserDto result = userService.createUser(registerDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getEmail()).isEqualTo("test@email.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User toSave = userCaptor.getValue();
        assertThat(toSave.getPassword()).isNotBlank();
        assertThat(toSave.getRoles()).extracting(Role::getName).contains("ROLE_USER");
    }

    @Test
    void createUser_conflict_whenEmailExists() {
        UserRegisterDto registerDto = new UserRegisterDto("a@a.com", "p", "p");
        when(userRepository.existsByEmail("a@a.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(registerDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_badRequest_whenPasswordsMismatch() {
        UserRegisterDto registerDto = new UserRegisterDto("a@a.com", "p1", "p2");

        assertThrows(PasswordMismatchException.class, () -> userService.createUser(registerDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ok_whenFound() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto dto = new UserDto(1L, "e@e.com", "f", "l");
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUserByEmail_lowercases_andFound() {
        User user = new User();
        user.setId(2L);
        when(userRepository.findByEmail("x@y.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(new UserDto(2L, "x@y.com", null, null));

        UserDto dto = userService.getUserByEmail("X@Y.com");
        assertThat(dto.getId()).isEqualTo(2L);
    }

    @Test
    void updateUser_appliesFields_andLowercasesEmail() {
        User existing = new User();
        existing.setId(5L);
        existing.setEmail("old@e.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        User updated = new User();
        updated.setId(5L);
        updated.setEmail("new@e.com");
        when(userRepository.save(existing)).thenReturn(updated);
        when(userMapper.toUserDto(updated)).thenReturn(new UserDto(5L, "new@e.com", "F", "L"));

        UserDto input = new UserDto(5L, "NEW@E.com", "F", "L");
        UserDto result = userService.updateUser(input);

        assertThat(result.getEmail()).isEqualTo("new@e.com");
        assertThat(existing.getEmail()).isEqualTo("new@e.com");
        assertThat(existing.getFirstName()).isEqualTo("F");
        assertThat(existing.getLastName()).isEqualTo("L");
    }

    @Test
    void getAllUsers_mapsList() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toDtoList(List.of(u1, u2))).thenReturn(List.of(
                new UserDto(1L, "a@a.com", null, null),
                new UserDto(2L, "b@b.com", null, null)
        ));

        List<UserDto> result = userService.getAllUsers();
        assertThat(result).hasSize(2);
    }

    @Test
    void getUserWithRoles_returnsDtoWithRoleNames() {
        Role r1 = new Role();
        r1.setId(1L);
        r1.setName("ROLE_USER");
        Role r2 = new Role();
        r2.setId(2L);
        r2.setName("ROLE_ADMIN");
        User user = new User();
        user.setId(7L);
        user.setEmail("e@e.com");
        user.setFirstName("F");
        user.setLastName("L");
        user.setRoles(Set.of(r1, r2));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        UserWithRolesDto dto = userService.getUserWithRoles(7L);
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void verifyCredentials_success() {
        String rawPassword = "secret";
        String hashed = PasswordUtil.hashPassword(rawPassword);
        User user = new User();
        user.setId(3L);
        user.setEmail("u@e.com");
        user.setPassword(hashed);
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(user));

        UserWithRolesDto dto = userService.verifyCredentials(new UserLoginDto("U@E.com", rawPassword));
        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getEmail()).isEqualTo("u@e.com");
    }

    @Test
    void verifyCredentials_invalidPassword() {
        String hashed = PasswordUtil.hashPassword("correct");
        User user = new User();
        user.setPassword(hashed);
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.verifyCredentials(new UserLoginDto("u@e.com", "wrong")));
    }

    @Test
    void verifyUserPassword_trueWhenMatches() {
        String raw = "pw";
        String hashed = PasswordUtil.hashPassword(raw);
        User user = new User();
        user.setPassword(hashed);
        when(userRepository.findByEmail("e@e.com")).thenReturn(Optional.of(user));

        boolean ok = userService.verifyUserPassword("e@e.com", raw);
        assertThat(ok).isTrue();
    }

    @Test
    void verifyUserPassword_throwsWhenUserMissing() {
        when(userRepository.findByEmail("missing@e.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.verifyUserPassword("missing@e.com", "x"));
    }
}


