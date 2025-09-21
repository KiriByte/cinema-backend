package org.kiribyte.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.kiribyte.exception.FeignClientException;
import org.kiribyte.model.ErrorResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public CustomErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Exception decode(String s, Response response) {

        try (InputStream inputStream = response.body().asInputStream()) {
            ErrorResponse errorResponse = objectMapper.readValue(inputStream, ErrorResponse.class);
            return new FeignClientException(
                    errorResponse.getErrorCode(),
                    errorResponse.getMessage(),
                    response.status()
            );

        } catch (IOException e) {
            return new FeignClientException(
                    "DECODING_ERROR",
                    "Failed to decode error response: " + e.getMessage(),
                    response.status());
        }
    }
}

