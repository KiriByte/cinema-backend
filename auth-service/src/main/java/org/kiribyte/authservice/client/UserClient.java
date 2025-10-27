package org.kiribyte.authservice.client;

import org.kiribyte.authservice.config.FeignConfig;
import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserLoginDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "user-service", path = "/api/v1/users", configuration = FeignConfig.class)
public interface UserClient {

    @PostMapping("/create")
    UserDto createUser(@RequestBody UserRegisterDto userRegisterDto);

    @GetMapping("/getById")
    UserDto getUserById(@RequestParam Long id);

    @PostMapping("/verify-credentials")
    UserWithRolesDto verifyCredentials(@RequestBody UserLoginDto userLoginDto);

    @GetMapping("/{id}/with-roles")
    UserWithRolesDto getUserWithRolesById(@PathVariable Long id);

}
