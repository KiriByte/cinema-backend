package org.kiribyte.hallservice.service;

import org.kiribyte.hallservice.dto.AddSeatTypeRequest;
import org.kiribyte.hallservice.dto.SeatTypeDto;

import java.util.List;

public interface SeatTypeService {

    SeatTypeDto getSeatTypeById(Long id);

    List<SeatTypeDto> getAllSeatType();

    SeatTypeDto addSeatType(AddSeatTypeRequest request);

    SeatTypeDto updateSeatType(Long id, SeatTypeDto seatTypeDto);

    void deleteSeatType(Long id);
}
