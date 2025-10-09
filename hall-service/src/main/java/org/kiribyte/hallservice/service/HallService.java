package org.kiribyte.hallservice.service;

import org.kiribyte.hallservice.dto.AddHallRequest;
import org.kiribyte.hallservice.dto.HallDto;
import org.kiribyte.hallservice.dto.HallWithSeatsDto;

import java.util.List;

public interface HallService {

    List<HallDto> getAllHalls();

    HallDto getHallById(Long id);

    HallWithSeatsDto getHallWithSeatsById(Long id);

    HallDto addHall(AddHallRequest hallDto);

    HallDto updateHallById(Long id, HallDto hallDto);

    void deleteHallById(Long id);


}
