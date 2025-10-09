package org.kiribyte.userservice.mapper;

import org.kiribyte.dto.UserDto;
import org.kiribyte.dto.UserRegisterDto;
import org.kiribyte.dto.UserWithRolesDto;
import org.kiribyte.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // User -> UserDto
    UserDto toUserDto(User user);

    // User -> UserWithRolesDto
    @Mapping(target = "roles", ignore = true)
    UserWithRolesDto toUserWithRolesDto(User user);

    // UserDto -> User
    //@Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(userDto.getEmail().toLowerCase())")
    User toUserFromUserDto(UserDto userDto);

    // UserRegisterDto -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(userRegisterDto.getEmail().toLowerCase())")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    User toUserFromRegisterDto(UserRegisterDto userRegisterDto);

    List<UserDto> toDtoList(List<User> users);

    List<User> toEntityList(List<UserDto> userDtos);
}
