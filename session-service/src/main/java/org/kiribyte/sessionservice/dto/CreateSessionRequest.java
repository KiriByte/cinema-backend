package org.kiribyte.sessionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionRequest {
    private UUID movieId;
    private Long hallId;
    private Instant startTime;
    private BigDecimal price;
}
