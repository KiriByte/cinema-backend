package org.kiribyte.movieservice.controller;

import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.kiribyte.movieservice.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable UUID id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/")
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MovieResponse addMovie(@RequestPart("movie") AddMovieRequest request,
                                  @RequestPart("file") MultipartFile file) {
        return movieService.addMovieWithPoster(request, file);
    }

    @PutMapping("/{id}")
    public MovieResponse updateMovie(
            @PathVariable UUID id,
            @RequestBody AddMovieRequest request) {
        return movieService.updateMovie(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
    }

    @PutMapping(value = "/{id}/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MovieResponse updateMoviePoster(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        return movieService.updatePoster(id, file);
    }

    @PostMapping("/tmdb/{tmdbId}")
    public MovieResponse addMovieByTmdbId(@PathVariable Integer tmdbId) {
        return movieService.addByTmdbId(tmdbId);
    }
}
