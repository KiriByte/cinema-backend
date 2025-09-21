package org.kiribyte.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithRolesDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;

}
