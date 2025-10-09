package org.kiribyte.hallservice.exception;

public class HallNotFoundException extends RuntimeException {
    public HallNotFoundException(String message) {
        super(message);
    }
}
