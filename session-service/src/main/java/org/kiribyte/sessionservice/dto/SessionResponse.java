package org.kiribyte.sessionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kiribyte.sessionservice.entity.SessionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    private UUID id;
    private UUID movieId;
    private Long hallId;
    private BigDecimal price;
    private Instant startTime;
    private Instant endTime;
    private SessionStatus status;
}
