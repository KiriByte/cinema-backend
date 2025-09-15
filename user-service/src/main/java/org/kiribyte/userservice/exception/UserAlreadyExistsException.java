package org.kiribyte.userservice.exception;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String email) {
        super("User with email already exists: " + email);
    }

}
