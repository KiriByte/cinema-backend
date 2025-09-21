package org.kiribyte.userservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.kiribyte.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "USER_ALREADY_EXISTS",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "PASSWORD_MISMATCH",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_CREDENTIALS",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "USER_EXCEPTION",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(RoleNotFoundException ex) {
        log.error("Role not found. Check database.", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "ROLE_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        log.error("INTERNAL_SERVER_ERROR", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal server error occurred."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
