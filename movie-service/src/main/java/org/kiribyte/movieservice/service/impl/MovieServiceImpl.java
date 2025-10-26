package org.kiribyte.movieservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.kiribyte.movieservice.entity.MovieEntity;
import org.kiribyte.movieservice.exception.InvalidMovieDataException;
import org.kiribyte.movieservice.exception.MovieAlreadyExistsException;
import org.kiribyte.movieservice.exception.MovieNotFoundException;
import org.kiribyte.movieservice.mapper.MovieMapper;
import org.kiribyte.movieservice.repository.MovieRepostiory;
import org.kiribyte.movieservice.service.MinioStorageService;
import org.kiribyte.movieservice.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieMapper movieMapper;
    private final MovieRepostiory movieRepository;
    private final MinioStorageService posterStorageService;

    public MovieServiceImpl(MovieMapper movieMapper,
                            MovieRepostiory movieRepository,
                            MinioStorageService posterStorageService) {
        this.movieMapper = movieMapper;
        this.movieRepository = movieRepository;
        this.posterStorageService = posterStorageService;
    }

    @Override
    public List<MovieResponse> getAllMovies() {
        List<MovieEntity> allMovies = movieRepository.findAllByOrderByCreatedAtDesc();
        List<MovieResponse> movieResponses = new ArrayList<>();
        for (MovieEntity movieEntity : allMovies) {
            var movieResponse = movieMapper.toResponse(movieEntity);
            var url = posterStorageService.getFileUrl(movieResponse.getId().toString());
            movieResponse.setPoster_url(url);
            movieResponses.add(movieResponse);
        }
        return movieResponses;
    }

    @Override
    public MovieResponse getMovieById(UUID id) {
        var movieEntity = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));
        var movieResponse = movieMapper.toResponse(movieEntity);
        var url = posterStorageService.getFileUrl(movieResponse.getId().toString());
        movieResponse.setPoster_url(url);
        return movieResponse;
    }

    @Transactional
    @Override
    public MovieResponse addMovie(AddMovieRequest request) {
        validateMovieRequest(request);
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
        validateMovieRequest(request);
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

    @Transactional
    @Override
    public void deleteMovie(UUID id) {
        MovieEntity existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        try {
            posterStorageService.delete(id.toString());
        } catch (Exception e) {
            log.error("Exception occurred while trying to delete movie with id: " + id, e);
        }

        movieRepository.deleteById(existingMovie.getId());
    }

    @Transactional
    @Override
    public MovieResponse addMovieWithPoster(AddMovieRequest request, MultipartFile multipartFile) {
        validateMovieRequest(request);
        MovieResponse movieResponse = addMovie(request);
        String url = null;
        if (multipartFile != null) {
            url = posterStorageService.upload(multipartFile, movieResponse.getId().toString());
        }
        movieResponse.setPoster_url(url);
        return movieResponse;
    }

    @Transactional
    @Override
    public MovieResponse updatePoster(UUID id, MultipartFile multipartFile) {
        MovieResponse movieResponse = getMovieById(id);
        String url = null;
        if (multipartFile != null) {
            url = posterStorageService.upload(multipartFile, movieResponse.getId().toString());
            movieResponse.setPoster_url(url);
        }
        return movieResponse;
    }

    private void validateMovieRequest(AddMovieRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InvalidMovieDataException("Movie title cannot be empty");
        }
        if (request.getDuration() <= 0) {
            throw new InvalidMovieDataException("Movie duration must be positive");
        }
    }
}
