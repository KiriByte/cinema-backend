package org.kiribyte.movieservice.exception;

public class InvalidMovieDataException extends RuntimeException {
    public InvalidMovieDataException(String message) {
        super(message);
    }

    public InvalidMovieDataException(String message, Throwable cause) {
    }
}
