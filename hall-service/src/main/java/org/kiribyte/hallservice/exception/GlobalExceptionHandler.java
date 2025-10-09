package org.kiribyte.hallservice.exception;

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

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotFoundException(SeatNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "SEAT NOT FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSeatTypeNotFoundException(SeatTypeNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "SEAT TYPE NOT FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HallNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHallNotFoundException(HallNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "SEAT NOT FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatTypeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSeatTypeAlreadyExistsException(SeatTypeAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                "SEAT TYPE ALREADY EXISTS",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SeatTypeInUseException.class)
    public ResponseEntity<ErrorResponse> handleSeatTypeInUseException(SeatTypeInUseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "SEAT TYPE IN USE",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
