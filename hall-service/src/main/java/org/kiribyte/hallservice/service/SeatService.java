package org.kiribyte.hallservice.service;

import org.kiribyte.hallservice.dto.SeatDto;

import java.util.List;

public interface SeatService {
    List<SeatDto> getAllSeats();

    SeatDto getSeatById(Long id);

    SeatDto createSeat(SeatDto seatDto);

    SeatDto updateSeat(Long id, SeatDto seatDto);

    void deleteSeat(Long id);


}
