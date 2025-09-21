package org.kiribyte.userservice.service;

import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserRegisterDto userRegisterDto);

    UserDto getUserById(Long id);

    UserDto getUserByEmail(String email);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long id);

    UserWithRolesDto getUserWithRoles(Long id);

    boolean verifyUserPassword(String email, String password);

    UserWithRolesDto verifyCredentials(UserLoginDto loginDto);
}
