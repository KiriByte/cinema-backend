package org.kiribyte.sessionservice.repostiory;

import org.kiribyte.sessionservice.entity.SessionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends CrudRepository<SessionEntity, UUID> {

    List<SessionEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT s FROM SessionEntity s WHERE s.hallId = :hallId " +
           "AND s.status = 'ACTIVE' " +
           "AND (:excludeSessionId IS NULL OR s.id != :excludeSessionId) " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<SessionEntity> findConflictingSessions(@Param("hallId") Long hallId,
                                               @Param("startTime") Instant startTime,
                                               @Param("endTime") Instant endTime,
                                               @Param("excludeSessionId") UUID excludeSessionId);

    @Query("SELECT s FROM SessionEntity s WHERE s.status = 'ACTIVE' " +
           "AND s.startTime <= :currentTime")
    List<SessionEntity> findSessionsToStart(@Param("currentTime") Instant currentTime);

    @Query("SELECT s FROM SessionEntity s WHERE s.status = 'ACTIVE' " +
           "AND s.endTime <= :currentTime")
    List<SessionEntity> findSessionsToComplete(@Param("currentTime") Instant currentTime);

    @Query("SELECT s FROM SessionEntity s WHERE s.status = 'ACTIVE' " +
           "ORDER BY s.createdAt DESC")
    List<SessionEntity> findActiveSessionsOrderByCreatedAtDesc();
}
