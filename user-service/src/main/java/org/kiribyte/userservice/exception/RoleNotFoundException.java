package org.kiribyte.userservice.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super("Role not found: " + message);
    }
}
