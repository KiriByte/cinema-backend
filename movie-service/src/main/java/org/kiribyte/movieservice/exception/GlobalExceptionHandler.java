package org.kiribyte.movieservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.kiribyte.exception.FeignClientException;
import org.kiribyte.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Requested endpoint not found: {} {}", ex.getHttpMethod(), ex.getResourcePath());

        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "ENDPOINT_NOT_FOUND",
                "The requested endpoint does not exist. Please check the URL and request method."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientException(FeignClientException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCode(),
                ex.getError(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getCode()));
    }


    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMovieNotFoundException(MovieNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "MOVIE_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MovieAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMovieAlreadyExistsException(MovieAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                "MOVIE_ALREADY_EXISTS",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidMovieDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMovieDataException(InvalidMovieDataException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "INVALID_MOVIE_DATA",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "FILE_UPLOAD_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<ErrorResponse> handleFileDownloadException(FileDownloadException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "FILE_DOWNLOAD_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileException(InvalidFileException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "INVALID_FILE",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "STORAGE_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StorageInitializationException.class)
    public ResponseEntity<ErrorResponse> handleStorageInitializationException(StorageInitializationException ex) {
        log.error("Storage initialization failed. Check configuration.", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "STORAGE_INITIALIZATION_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UrlGenerationException.class)
    public ResponseEntity<ErrorResponse> handleUrlGenerationException(UrlGenerationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "URL_GENERATION_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
