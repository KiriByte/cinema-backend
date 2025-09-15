package org.kiribyte.userservice.service.impl;

import org.kiribyte.userservice.dto.LoginDto;
import org.kiribyte.userservice.dto.UserDto;
import org.kiribyte.userservice.dto.UserRegisterDto;
import org.kiribyte.userservice.exception.PasswordMismatchException;
import org.kiribyte.userservice.exception.UserAlreadyExistsException;
import org.kiribyte.userservice.exception.UserNotFoundException;
import org.kiribyte.userservice.mapper.UserMapper;
import org.kiribyte.userservice.model.User;
import org.kiribyte.userservice.repostory.UserRepository;
import org.kiribyte.userservice.service.UserService;
import org.kiribyte.userservice.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
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
    public UserDto verifyCredentials(LoginDto loginDto) {
        return null;
    }
}
