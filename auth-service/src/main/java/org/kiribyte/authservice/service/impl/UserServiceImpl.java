package org.kiribyte.authservice.service.impl;

import lombok.NonNull;
import org.kiribyte.authservice.entity.Role;
import org.kiribyte.authservice.entity.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl {

    private final List<User> users;

    public UserServiceImpl() {
        this.users = List.of(
                new User((long) 1, "kmisyuro@gmail.com", "1234", "Kirill", "Misyuro", Collections.singleton(new Role((long) 1, "ADMIN"))),
                new User((long) 2, "anton", "1234", "Антон", "Иванов", Collections.singleton(new Role((long) 1, "ADMIN"))));
    }

    public Optional<User> getByLogin(@NonNull String email) {
        return users.stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }
}
