package org.kiribyte.hallservice.controller;

import org.kiribyte.hallservice.dto.AddHallRequest;
import org.kiribyte.hallservice.dto.HallDto;
import org.kiribyte.hallservice.dto.HallWithSeatsDto;
import org.kiribyte.hallservice.service.HallService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/halls")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public List<HallDto> getAll() {
        return hallService.getAllHalls();
    }

    @GetMapping("/{id}")
    public HallDto getHallById(@PathVariable Long id) {
        return hallService.getHallById(id);
    }

    @GetMapping("/{id}/seats")
    public HallWithSeatsDto getHallWithSeats(@PathVariable Long id) {
        return hallService.getHallWithSeatsById(id);
    }

    @PostMapping
    public HallDto addHall(@RequestBody AddHallRequest hallDto) {
        return hallService.addHall(hallDto);
    }

    @PutMapping("/{id}")
    public HallDto updateHall(@PathVariable Long id, @RequestBody HallDto hallDto) {
        return hallService.updateHallById(id, hallDto);
    }

    @DeleteMapping("/{id}")
    public void deleteHall(@PathVariable Long id) {
        hallService.deleteHallById(id);
    }


}
