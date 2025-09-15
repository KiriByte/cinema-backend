package org.kiribyte.authservice.controller;

import org.kiribyte.authservice.dto.LoginDto;
import org.kiribyte.authservice.dto.UserRegisterDto;
import org.kiribyte.authservice.dto.TokensDto;
import org.kiribyte.authservice.entity.User;
import org.kiribyte.authservice.service.impl.TokenServiceImpl;
import org.kiribyte.authservice.service.impl.UserServiceImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final TokenServiceImpl tokenService;

    public AuthController(UserServiceImpl userService, TokenServiceImpl tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/login")
    public TokensDto login(@RequestBody LoginDto loginDto) {

        if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        User user = userService.getByLogin(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        if (!user.checkPassword(loginDto.getPassword())) {
//            throw new BadCredentialsException("Invalid password");
//        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return new TokensDto(accessToken, refreshToken);
    }

    @PostMapping("/register")
    public TokensDto register(@RequestBody UserRegisterDto userRegisterDto) {

        return new TokensDto();
    }

    @PostMapping("/logout")
    public void logout() {

    }

    @PostMapping("/refresh")
    public TokensDto refresh(@RequestBody String refreshToken) {

        return new TokensDto();
    }
}
