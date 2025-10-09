package org.kiribyte.hallservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HallWithSeatsDto {
    private Long id;
    private String name;
    private String description;
    private List<SeatDto> seats = new ArrayList<>();
}
