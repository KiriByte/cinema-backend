package org.kiribyte.userservice.exception;

public class PasswordMismatchException extends UserException {
    public PasswordMismatchException() {
        super("Password and confirmation password do not match");
    }
}
