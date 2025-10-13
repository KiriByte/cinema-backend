package org.kiribyte.movieservice.repository;

import org.kiribyte.movieservice.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepostiory extends JpaRepository<MovieEntity, UUID> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, UUID id);
}
