package org.kiribyte.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
