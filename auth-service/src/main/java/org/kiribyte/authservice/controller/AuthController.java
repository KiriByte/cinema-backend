package org.kiribyte.authservice.controller;

import org.kiribyte.authservice.dto.RefreshTokenRequest;
import org.kiribyte.authservice.dto.TokensDto;
import org.kiribyte.authservice.service.impl.AuthServiceImpl;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public TokensDto login(@RequestBody UserLoginDto loginDto) {

        return authService.login(loginDto);

    }

    @PostMapping("/register")
    public TokensDto register(@RequestBody UserRegisterDto userRegisterDto) {
        return authService.register(userRegisterDto);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest);
    }

    @PostMapping("/refresh")
    public TokensDto refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refresh(refreshTokenRequest);
    }
}
