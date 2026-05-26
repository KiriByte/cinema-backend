package org.kiribyte.movieservice.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfigTmdb {

    @Value("${tmdb.apiKey}")
    private String tmdbApiKey;

    @Bean
    public RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + tmdbApiKey);
            requestTemplate.header("accept", "application/json");
        };
    }
}
