package org.kiribyte.hallservice.repository;

import org.kiribyte.hallservice.entity.SeatTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatTypeEntity,Long> {
    boolean existsByName(String name);
}
