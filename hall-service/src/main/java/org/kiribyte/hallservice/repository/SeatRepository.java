package org.kiribyte.hallservice.repository;

import org.kiribyte.hallservice.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findBySeatTypeId(Long id);

    @Query("SELECT s FROM SeatEntity s WHERE s.hallId.id = :hallId")
    List<SeatEntity> findByHallId(@Param("hallId") Long hallId);
}
