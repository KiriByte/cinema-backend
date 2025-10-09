package org.kiribyte.hallservice.exception;

public class SeatTypeNotFoundException extends RuntimeException {
    public SeatTypeNotFoundException(String message) {
        super(message);
    }
}
