package org.kiribyte.userservice.service.impl;

import jakarta.transaction.Transactional;
import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.kiribyte.userservice.exception.*;
import org.kiribyte.userservice.mapper.UserMapper;
import org.kiribyte.userservice.entity.Role;
import org.kiribyte.userservice.entity.User;
import org.kiribyte.userservice.repostory.RoleRepository;
import org.kiribyte.userservice.repostory.UserRepository;
import org.kiribyte.userservice.service.UserService;
import org.kiribyte.userservice.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByEmail(userRegisterDto.getEmail().toLowerCase())) {
            throw new UserAlreadyExistsException(userRegisterDto.getEmail());
        }
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        var user = userMapper.toUserFromRegisterDto(userRegisterDto);
        user.setPassword(PasswordUtil.hashPassword(userRegisterDto.getPassword()));

        assignDefaultRole(user);

        User save = userRepository.save(user);
        return userMapper.toUserDto(save);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(email, "email"));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> all = userRepository.findAll();
        return userMapper.toDtoList(all);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException(userDto.getId()));
        existingUser.setEmail(userDto.getEmail().toLowerCase());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    public UserWithRolesDto getUserWithRoles(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return new UserWithRolesDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }


    @Override
    public UserWithRolesDto verifyCredentials(UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.getEmail().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!PasswordUtil.verifyPassword(userLoginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return new UserWithRolesDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public boolean verifyUserPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email, "email"));
        return PasswordUtil.verifyPassword(password, user.getPassword());
    }

    private void assignDefaultRole(User user) {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
        user.addRole(userRole);
    }


}
