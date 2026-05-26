package org.kiribyte.movieservice.service;

import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MovieService {

    void deleteMovie(UUID id);

    MovieResponse updateMovie(UUID id, AddMovieRequest request);

    List<MovieResponse> getAllMovies();

    MovieResponse getMovieById(UUID id);

    MovieResponse addMovie(AddMovieRequest request);

    MovieResponse addMovieWithPoster(AddMovieRequest request, MultipartFile multipartFile);

    MovieResponse updatePoster(UUID id, MultipartFile multipartFile);

    MovieResponse addByTmdbId(Integer id);
}
