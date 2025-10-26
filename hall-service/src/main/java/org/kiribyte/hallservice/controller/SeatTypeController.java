package org.kiribyte.hallservice.controller;

import org.kiribyte.hallservice.dto.AddSeatTypeRequest;
import org.kiribyte.hallservice.dto.SeatTypeDto;
import org.kiribyte.hallservice.service.SeatTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seat-types")
public class SeatTypeController {

    private final SeatTypeService seatTypeService;

    public SeatTypeController(SeatTypeService seatTypeService) {
        this.seatTypeService = seatTypeService;
    }

    @GetMapping
    public List<SeatTypeDto> getAll() {
        return seatTypeService.getAllSeatType();
    }

    @GetMapping("/{id}")
    public SeatTypeDto getSeatTypeById(@PathVariable Long id) {
        return seatTypeService.getSeatTypeById(id);
    }

    @PostMapping
    public SeatTypeDto addSeatType(@RequestBody AddSeatTypeRequest seatTypeRequest) {
        return seatTypeService.addSeatType(seatTypeRequest);
    }

    @PutMapping("/{id}")
    public SeatTypeDto updateSeatType(@PathVariable Long id, @RequestBody SeatTypeDto seatTypeDto) {
        return seatTypeService.updateSeatType(id, seatTypeDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSeatType(@PathVariable Long id) {
        seatTypeService.deleteSeatType(id);
    }
}
