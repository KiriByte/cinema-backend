package org.kiribyte.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Integer code;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(Integer code, String error, String message) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
