package org.kiribyte.userservice.service;

import org.kiribyte.userservice.dto.LoginDto;
import org.kiribyte.userservice.dto.UserDto;
import org.kiribyte.userservice.dto.UserRegisterDto;
import org.kiribyte.userservice.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserRegisterDto userRegisterDto);

    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long id);

    UserDto findByEmailAndVerifyPassword(String email, String Password);
}
