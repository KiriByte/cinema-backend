package org.kiribyte.hallservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {
    private Long id;
    private Long hallId;
    private Long rowNumber;
    private Long seatNumber;
    private Long seatTypeId;
}
