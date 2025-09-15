package org.kiribyte.userservice.exception;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String identifier, String type) {
        super("User not found with " + type + ": " + identifier);
    }
}
