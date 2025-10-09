package org.kiribyte.hallservice.repository;

import org.kiribyte.hallservice.entity.HallEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<HallEntity, Long> {

    @Query("""
            SELECT h FROM HallEntity h
            LEFT JOIN FETCH h.seats s
            LEFT JOIN FETCH s.seatType
            WHERE h.id = :hallId
            """)
    Optional<HallEntity> findHallWithSeatsAndTypeById(Long id);
}
