package org.kiribyte.movieservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.kiribyte.movieservice.entity.MovieEntity;
import org.kiribyte.movieservice.exception.InvalidMovieDataException;
import org.kiribyte.movieservice.exception.MovieAlreadyExistsException;
import org.kiribyte.movieservice.exception.MovieNotFoundException;
import org.kiribyte.movieservice.mapper.MovieMapper;
import org.kiribyte.movieservice.repository.MovieRepostiory;
import org.kiribyte.movieservice.service.impl.MinioPosterStorageServiceImpl;
import org.kiribyte.movieservice.service.impl.MovieServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private MovieRepostiory movieRepository;

    @Mock
    private MinioPosterStorageServiceImpl posterStorageService;

    @InjectMocks
    private MovieServiceImpl movieService;

    private MovieEntity movieEntity;
    private MovieResponse movieResponse;
    private AddMovieRequest addMovieRequest;
    private UUID movieId;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        
        movieEntity = new MovieEntity();
        movieEntity.setId(movieId);
        movieEntity.setTitle("Test Movie");
        movieEntity.setDescription("Test Description");
        movieEntity.setDuration(120);

        movieResponse = new MovieResponse();
        movieResponse.setId(movieId);
        movieResponse.setTitle("Test Movie");
        movieResponse.setDescription("Test Description");
        movieResponse.setDuration(120);
        movieResponse.setPoster_url("http://url.com/poster.jpg");

        addMovieRequest = new AddMovieRequest();
        addMovieRequest.setTitle("Test Movie");
        addMovieRequest.setDescription("Test Description");
        addMovieRequest.setDuration(120);
    }

    @Test
    void getAllMovies_ShouldReturnAllMoviesWithPosterUrls() {
        // Arrange
        List<MovieEntity> movieEntities = Arrays.asList(movieEntity);
        when(movieRepository.findAllByOrderByCreatedAtDesc()).thenReturn(movieEntities);
        when(movieMapper.toResponse(movieEntity)).thenReturn(movieResponse);
        when(posterStorageService.getFileUrl(movieId.toString())).thenReturn("http://url.com/poster.jpg");

        // Act
        List<MovieResponse> result = movieService.getAllMovies();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieResponse, result.get(0));
        verify(movieRepository).findAllByOrderByCreatedAtDesc();
        verify(movieMapper).toResponse(movieEntity);
        verify(posterStorageService).getFileUrl(movieId.toString());
    }

    @Test
    void getMovieById_WhenMovieExists_ShouldReturnMovieWithPosterUrl() {
        // Arrange
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movieEntity));
        when(movieMapper.toResponse(movieEntity)).thenReturn(movieResponse);
        when(posterStorageService.getFileUrl(movieId.toString())).thenReturn("http://url.com/poster.jpg");

        // Act
        MovieResponse result = movieService.getMovieById(movieId);

        // Assert
        assertNotNull(result);
        assertEquals(movieResponse, result);
        verify(movieRepository).findById(movieId);
        verify(movieMapper).toResponse(movieEntity);
        verify(posterStorageService).getFileUrl(movieId.toString());
    }

    @Test
    void getMovieById_WhenMovieDoesNotExist_ShouldThrowMovieNotFoundException() {
        // Arrange
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        // Act & Assert
        MovieNotFoundException exception = assertThrows(MovieNotFoundException.class, 
            () -> movieService.getMovieById(movieId));
        
        assertEquals("Movie not found", exception.getMessage());
        verify(movieRepository).findById(movieId);
        verify(movieMapper, never()).toResponse(any());
        verify(posterStorageService, never()).getFileUrl(any());
    }

    @Test
    void addMovie_WithValidRequest_ShouldCreateAndReturnMovie() {
        // Arrange
        when(movieRepository.existsByTitle(addMovieRequest.getTitle())).thenReturn(false);
        when(movieMapper.toEntity(addMovieRequest)).thenReturn(movieEntity);
        when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
        when(movieMapper.toResponse(movieEntity)).thenReturn(movieResponse);

        // Act
        MovieResponse result = movieService.addMovie(addMovieRequest);

        // Assert
        assertNotNull(result);
        assertEquals(movieResponse, result);
        verify(movieRepository).existsByTitle(addMovieRequest.getTitle());
        verify(movieMapper).toEntity(addMovieRequest);
        verify(movieRepository).save(movieEntity);
        verify(movieMapper).toResponse(movieEntity);
    }

    @Test
    void addMovie_WhenMovieWithSameTitleExists_ShouldThrowMovieAlreadyExistsException() {
        // Arrange
        when(movieRepository.existsByTitle(addMovieRequest.getTitle())).thenReturn(true);

        // Act & Assert
        MovieAlreadyExistsException exception = assertThrows(MovieAlreadyExistsException.class,
            () -> movieService.addMovie(addMovieRequest));
        
        assertEquals("Movie with title 'Test Movie' already exists", exception.getMessage());
        verify(movieRepository).existsByTitle(addMovieRequest.getTitle());
        verify(movieMapper, never()).toEntity(any());
        verify(movieRepository, never()).save(any());
    }

    @Test
    void addMovie_WithInvalidData_ShouldThrowInvalidMovieDataException() {
        // Arrange
        AddMovieRequest invalidRequest = new AddMovieRequest();
        invalidRequest.setTitle(""); // Empty title
        invalidRequest.setDescription("Test Description");
        invalidRequest.setDuration(120);

        // Act & Assert
        InvalidMovieDataException exception = assertThrows(InvalidMovieDataException.class,
            () -> movieService.addMovie(invalidRequest));
        
        assertEquals("Movie title cannot be empty", exception.getMessage());
        verify(movieRepository, never()).existsByTitle(any());
        verify(movieMapper, never()).toEntity(any());
        verify(movieRepository, never()).save(any());
    }

    @Test
    void deletePoster_WhenMovieExists_ShouldDeletePosterFromStorage() {
        // Arrange
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movieEntity));

        // Act
        movieService.deleteMovie(movieId);

        // Assert
        verify(movieRepository).findById(movieId);
        verify(posterStorageService).delete(movieId.toString());
    }

}
