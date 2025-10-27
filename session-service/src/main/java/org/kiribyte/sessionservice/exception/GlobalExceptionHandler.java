package org.kiribyte.sessionservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.kiribyte.exception.FeignClientException;
import org.kiribyte.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientException(FeignClientException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCode(),
                ex.getError(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getCode()));
    }

    @ExceptionHandler(SessionTimeConflictException.class)
    public ResponseEntity<ErrorResponse> handleSessionTimeConflictException(SessionTimeConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                "SESSION TIME CONFLICT",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFoundException(SessionNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "SESSION NOT FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
