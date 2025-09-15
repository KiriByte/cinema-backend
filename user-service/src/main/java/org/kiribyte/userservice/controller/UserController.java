package org.kiribyte.userservice.controller;

import org.kiribyte.userservice.dto.UserDto;
import org.kiribyte.userservice.dto.UserRegisterDto;
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

    @GetMapping("/getAll")
    public List<UserDto> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/getById")
    public UserDto getById(@RequestParam Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/getByEmail")
    public UserDto getByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/update")
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/verifyCredentials")
    public UserDto verifyCredentials(@RequestBody LoginDto loginDto){
        return new UserDto();
    }
}
