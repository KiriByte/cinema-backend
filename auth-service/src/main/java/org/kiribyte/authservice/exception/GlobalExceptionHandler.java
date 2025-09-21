package org.kiribyte.authservice.exception;

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
//    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
//    public ResponseEntity<Object> handleAuthException(Exception ex) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(ex.getMessage());
//    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Object> handleBadRequest(Exception ex) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ex.getMessage());
//    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientException(FeignClientException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> expiredTokenException(TokenExpiredException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "TOKEN_EXPIRED",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ErrorResponse> expiredTokenException(TokenValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "TOKEN_INVALID",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
