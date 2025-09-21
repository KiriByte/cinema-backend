package org.kiribyte.userservice.controller;

import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.kiribyte.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.createUser(userRegisterDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping(params = "email")
    public UserDto getByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/verify-credentials")
    public UserWithRolesDto verifyCredentials(@RequestBody UserLoginDto loginDto) {
        return userService.verifyCredentials(loginDto);
    }

    @GetMapping("/{id}/with-roles")
    public UserWithRolesDto getUserWithRolesById(@PathVariable Long id){
        return userService.getUserWithRoles(id);
    }
}
