package org.kiribyte.authservice.repository;

import org.kiribyte.authservice.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    //TokenEntity findByUserId(Long id);
    Optional<TokenEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
