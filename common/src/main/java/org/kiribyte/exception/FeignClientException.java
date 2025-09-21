package org.kiribyte.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeignClientException extends RuntimeException {
    private String errorCode;
    private String message;
    private int statusCode;

}
