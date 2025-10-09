package org.kiribyte.hallservice.controller;

import org.kiribyte.hallservice.dto.SeatDto;
import org.kiribyte.hallservice.service.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public List<SeatDto> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/{id}")
    public SeatDto getSeatById(@PathVariable Long id) {
        return seatService.getSeatById(id);
    }

    @PostMapping
    public SeatDto createSeat(@RequestBody SeatDto seatDto) {
        return seatService.createSeat(seatDto);
    }

    @PutMapping("/{id}")
    public SeatDto updateSeat(@PathVariable Long id,@RequestBody SeatDto seatDto) {
        return seatService.updateSeat(id, seatDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }
}