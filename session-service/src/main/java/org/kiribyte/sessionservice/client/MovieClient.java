package org.kiribyte.sessionservice.client;

import org.kiribyte.dto.MovieDto;
import org.kiribyte.sessionservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "movie-service", path = "/api/v1/movies", configuration = FeignConfig.class, url = "http://localhost:8080")
public interface MovieClient {

    @GetMapping("/{id}")
    MovieDto getMovieById(@PathVariable UUID id);

}
