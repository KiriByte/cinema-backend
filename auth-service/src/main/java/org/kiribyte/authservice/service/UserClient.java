package org.kiribyte.authservice.service;

import org.kiribyte.authservice.dto.UserDto;
import org.kiribyte.authservice.dto.UserRegisterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserClient {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody UserRegisterDto userRegisterDto);

    @GetMapping("/getById")
    UserDto getUserById(@RequestParam("id") Long id);

}
