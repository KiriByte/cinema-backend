package org.kiribyte.movieservice.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
    public FileUploadException(String message, Throwable cause) {}
}
