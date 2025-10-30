package org.kiribyte.apigateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.kiribyte.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                403,
                "INVALID_TOKEN",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("INTERNAL_SERVER_ERROR", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal server error occurred."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
