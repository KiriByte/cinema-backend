package org.kiribyte.movieservice.service.impl;

import jakarta.transaction.Transactional;
import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.kiribyte.movieservice.entity.MovieEntity;
import org.kiribyte.movieservice.exception.MovieAlreadyExistsException;
import org.kiribyte.movieservice.exception.MovieNotFoundException;
import org.kiribyte.movieservice.mapper.MovieMapper;
import org.kiribyte.movieservice.repository.MovieRepostiory;
import org.kiribyte.movieservice.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MovieServiceImpl implements MovieService {

    public final MovieMapper movieMapper;
    public final MovieRepostiory movieRepository;

    public MovieServiceImpl(MovieMapper movieMapper, MovieRepostiory movieRepository) {
        this.movieMapper = movieMapper;
        this.movieRepository = movieRepository;
    }

    @Override
    public List<MovieResponse> getAllMovies() {
        List<MovieEntity> allMovies = movieRepository.findAll();
        List<MovieResponse> movieResponses = new ArrayList<>();
        for (MovieEntity movieEntity : allMovies) {
            var movieResponse = movieMapper.toResponse(movieEntity);
            movieResponses.add(movieResponse);
        }
        return movieResponses;
    }

    @Override
    public MovieResponse getMovieById(UUID id) {
        var movieEntity = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));
        return movieMapper.toResponse(movieEntity);
    }

    @Transactional
    @Override
    public MovieResponse addMovie(AddMovieRequest request) {

        if (movieRepository.existsByTitle(request.getTitle())) {
            throw new MovieAlreadyExistsException("Movie with title '" + request.getTitle() + "' already exists");
        }
        MovieEntity movie = movieMapper.toEntity(request);
        MovieEntity savedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(savedMovie);

    }

    @Transactional
    @Override
    public MovieResponse updateMovie(UUID id, AddMovieRequest request) {
        MovieEntity existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        // Проверка на конфликт названий (если название изменилось)
        if (!existingMovie.getTitle().equals(request.getTitle()) &&
                movieRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new MovieAlreadyExistsException("Movie with title '" + request.getTitle() + "' already exists");
        }
        existingMovie.setTitle(request.getTitle());
        existingMovie.setDescription(request.getDescription());
        existingMovie.setDuration(request.getDuration());
        MovieEntity updatedMovie = movieRepository.save(existingMovie);
        return movieMapper.toResponse(updatedMovie);
    }

    @Override
    public void deleteMovie(UUID id) {
        movieRepository.deleteById(id);
    }

    @Override
    public MovieResponse addMovieWithPoster(AddMovieRequest request, MultipartFile multipartFile) {
        return null;
    }

    @Override
    public MovieResponse updatePoster(UUID id, MultipartFile multipartFile) {
        return null;
    }
}
