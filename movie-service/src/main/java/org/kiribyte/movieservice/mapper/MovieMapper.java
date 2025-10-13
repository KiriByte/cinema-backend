package org.kiribyte.movieservice.mapper;

import org.kiribyte.movieservice.dto.AddMovieRequest;
import org.kiribyte.movieservice.dto.MovieResponse;
import org.kiribyte.movieservice.entity.MovieEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieEntity toEntity(AddMovieRequest request);

    MovieResponse toResponse(MovieEntity entity);
}
