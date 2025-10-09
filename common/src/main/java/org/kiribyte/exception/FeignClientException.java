package org.kiribyte.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeignClientException extends RuntimeException {
    private Integer code;
    private String error;
    private String message;

}
