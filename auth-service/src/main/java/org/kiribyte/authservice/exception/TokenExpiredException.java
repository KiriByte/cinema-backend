package org.kiribyte.authservice.exception;

public class TokenExpiredException extends TokenValidationException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
