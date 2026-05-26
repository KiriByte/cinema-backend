package org.kiribyte.movieservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.kiribyte.movieservice.dto.TmdbMovieResponse;
import org.kiribyte.movieservice.dto.tmdb.ExternalTmdbMovie;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MovieServiceImpl implements MovieService {

    private static final String TMDB_IMAGE_URL = "https://image.tmdb.org/t/p/original";

    private final MovieMapper movieMapper;
    private final MovieRepostiory movieRepository;
    private final MinioStorageService posterStorageService;
    private final TmdbService tmdbService;

    public MovieServiceImpl(MovieMapper movieMapper,
                            MovieRepostiory movieRepository,
                            MinioStorageService posterStorageService, TmdbService tmdbService) {
        this.movieMapper = movieMapper;
        this.movieRepository = movieRepository;
        this.posterStorageService = posterStorageService;
        this.tmdbService = tmdbService;
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

    @Transactional
    @Override
    public MovieResponse addByTmdbId(Integer id) {
        ExternalTmdbMovie tmdbMovie = tmdbService.getMovieById(id);
        if (tmdbMovie == null) {
            throw new MovieNotFoundException("Movie not found in TMDB with id: " + id);
        }

        AddMovieRequest request = new AddMovieRequest();
        request.setTitle(tmdbMovie.getTitle());
        request.setDescription(tmdbMovie.getOverview() != null && !tmdbMovie.getOverview().isEmpty() 
                ? tmdbMovie.getOverview() 
                : "No description available");
        request.setDuration(tmdbMovie.getRuntime() != null && tmdbMovie.getRuntime() > 0 
                ? tmdbMovie.getRuntime() 
                : 90);

        validateMovieRequest(request);

        if (movieRepository.existsByTitle(request.getTitle())) {
            throw new MovieAlreadyExistsException("Movie with title '" + request.getTitle() + "' already exists");
        }

        MovieEntity movie = movieMapper.toEntity(request);
        MovieEntity savedMovie = movieRepository.save(movie);
        MovieResponse movieResponse = movieMapper.toResponse(savedMovie);

        String posterUrl = null;
        if (tmdbMovie.getPoster_path() != null && !tmdbMovie.getPoster_path().isEmpty()) {
            try {
                String tmdbImageUrl = TMDB_IMAGE_URL + tmdbMovie.getPoster_path();
                byte[] imageBytes = downloadImageFromUrl(tmdbImageUrl);
                if (imageBytes != null) {
                    String contentType = getContentTypeFromUrl(tmdbImageUrl);
                    posterUrl = posterStorageService.upload(imageBytes, movieResponse.getId().toString(), contentType);
                }
            } catch (Exception e) {
                log.error("Failed to download and upload poster for movie with id: {}", movieResponse.getId(), e);
            }
        }
        
        movieResponse.setPoster_url(posterUrl);
        return movieResponse;
    }

    private byte[] downloadImageFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            try (InputStream inputStream = url.openStream()) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            log.error("Failed to download image from URL: {}", imageUrl, e);
            return null;
        }
    }

    private String getContentTypeFromUrl(String imageUrl) {
        if (imageUrl.endsWith(".png")) {
            return "image/png";
        }
        return "image/jpeg";
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
