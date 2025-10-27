package org.kiribyte.sessionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sessions", schema = "session_service_schema")
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "movie_id", nullable = false)
    private UUID movieId;
    @Column(name = "hall_id", nullable = false)
    private Long hallId;
    @Column(name = "start_time", nullable = false)
    private Instant startTime;
    @Column(name = "end_time", nullable = false)
    private Instant endTime;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
