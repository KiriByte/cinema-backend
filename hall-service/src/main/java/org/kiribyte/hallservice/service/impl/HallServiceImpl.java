package org.kiribyte.hallservice.service.impl;

import org.kiribyte.hallservice.dto.AddHallRequest;
import org.kiribyte.hallservice.dto.HallDto;
import org.kiribyte.hallservice.dto.HallWithSeatsDto;
import org.kiribyte.hallservice.entity.HallEntity;
import org.kiribyte.hallservice.entity.SeatEntity;
import org.kiribyte.hallservice.exception.HallNotFoundException;
import org.kiribyte.hallservice.mapper.HallMapper;
import org.kiribyte.hallservice.repository.HallRepository;
import org.kiribyte.hallservice.repository.SeatRepository;
import org.kiribyte.hallservice.service.HallService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;
    private final HallMapper hallMapper;
    private  final SeatRepository seatRepository;

    public HallServiceImpl(HallRepository hallRepository, HallMapper hallMapper, SeatRepository seatRepository) {
        this.hallRepository = hallRepository;
        this.hallMapper = hallMapper;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<HallDto> getAllHalls() {
        var halls = hallRepository.findAll();
        return hallMapper.entitiesToDtos(halls);
    }

    @Override
    public HallDto getHallById(Long id) {
        HallEntity hall = hallRepository.findById(id)
                .orElseThrow(() -> new HallNotFoundException("Hall not found"));
        return hallMapper.entityToDto(hall);
    }

    @Override
    public HallWithSeatsDto getHallWithSeatsById(Long id) {
        HallEntity hallsWithSeats = hallRepository.findHallWithSeatsAndTypeById(id)
                .orElseThrow(() -> new HallNotFoundException("Hall not found"));
        return hallMapper.entityToDtoWithSeats(hallsWithSeats);
    }

    @Override
    public HallDto addHall(AddHallRequest hallDto) {
        HallEntity hall = hallMapper.addRequestToEntity(hallDto);
        HallEntity savedHall = hallRepository.save(hall);
        return hallMapper.entityToDto(savedHall);
    }

    @Override
    public HallDto updateHallById(Long id, HallDto hallDto) {
        HallEntity existingHall = hallRepository.findById(id)
                .orElseThrow(() -> new HallNotFoundException("Hall not found with id: " + id));

        hallMapper.updateEntityFromDto(hallDto, existingHall);
        HallEntity updatedHall = hallRepository.save(existingHall);
        return hallMapper.entityToDto(updatedHall);
    }

    @Override
    public void deleteHallById(Long id) {
        List<SeatEntity> seats = seatRepository.findByHallId(id);
        seatRepository.deleteAll(seats);
        hallRepository.deleteById(id);
    }
}
