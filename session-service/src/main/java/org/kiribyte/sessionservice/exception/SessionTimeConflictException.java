package org.kiribyte.sessionservice.exception;

public class SessionTimeConflictException extends RuntimeException {
    
    public SessionTimeConflictException(String message) {
        super(message);
    }
    
    public SessionTimeConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

