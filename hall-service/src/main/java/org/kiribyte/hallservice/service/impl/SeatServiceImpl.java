package org.kiribyte.hallservice.service.impl;

import org.kiribyte.hallservice.dto.SeatDto;
import org.kiribyte.hallservice.entity.HallEntity;
import org.kiribyte.hallservice.entity.SeatEntity;
import org.kiribyte.hallservice.entity.SeatTypeEntity;
import org.kiribyte.hallservice.exception.HallNotFoundException;
import org.kiribyte.hallservice.exception.SeatNotFoundException;
import org.kiribyte.hallservice.exception.SeatTypeNotFoundException;
import org.kiribyte.hallservice.mapper.SeatMapper;
import org.kiribyte.hallservice.repository.HallRepository;
import org.kiribyte.hallservice.repository.SeatRepository;
import org.kiribyte.hallservice.repository.SeatTypeRepository;
import org.kiribyte.hallservice.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final SeatTypeRepository seatTypeRepository;
    private final HallRepository hallRepository;

    public SeatServiceImpl(SeatRepository seatRepository, SeatMapper seatMapper, SeatTypeRepository seatTypeRepository, HallRepository hallRepository) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
        this.seatTypeRepository = seatTypeRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public List<SeatDto> getAllSeats() {
        List<SeatEntity> seatEntities = seatRepository.findAll();
        return seatMapper.entitiesToDtos(seatEntities);
    }

    @Override
    public SeatDto getSeatById(Long id) {
        SeatEntity seatEntity = seatRepository.findById(id)
                .orElseThrow(()-> new SeatNotFoundException("Seat not found"));
        return seatMapper.entityToDto(seatEntity);
    }

    @Override
    public SeatDto createSeat(SeatDto seatDto) {
        // Проверяем существование зала
        HallEntity hall = hallRepository.findById(seatDto.getHallId())
                .orElseThrow(() -> new HallNotFoundException("Hall not found with id: " + seatDto.getHallId()));

        // Проверяем существование типа места
        SeatTypeEntity seatType = null;
        if (seatDto.getSeatTypeId() != null) {
            seatType = seatTypeRepository.findById(seatDto.getSeatTypeId())
                    .orElseThrow(() -> new SeatTypeNotFoundException("Seat type not found with id: " + seatDto.getSeatTypeId()));
        }

        SeatEntity entity = seatMapper.dtoToEntity(seatDto);
        entity.setHallId(hall);
        entity.setSeatType(seatType);

        SeatEntity savedEntity = seatRepository.save(entity);
        return seatMapper.entityToDto(savedEntity);
    }

    @Override
    public SeatDto updateSeat(Long id, SeatDto seatDto) {
        SeatEntity existingSeat = seatRepository.findById(id)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with id: " + id));

        // Проверяем существование зала, если он меняется
        if (!existingSeat.getHallId().getId().equals(seatDto.getHallId())) {
            HallEntity hall = hallRepository.findById(seatDto.getHallId())
                    .orElseThrow(() -> new HallNotFoundException("Hall not found with id: " + seatDto.getHallId()));
            existingSeat.setHallId(hall);
        }

        // Проверяем существование типа места, если он меняется
        if (seatDto.getSeatTypeId() != null) {
            SeatTypeEntity seatType = seatTypeRepository.findById(seatDto.getSeatTypeId())
                    .orElseThrow(() -> new SeatTypeNotFoundException("Seat type not found with id: " + seatDto.getSeatTypeId()));
            existingSeat.setSeatType(seatType);
        } else {
            existingSeat.setSeatType(null);
        }

        existingSeat.setRowNumber(seatDto.getRowNumber());
        existingSeat.setSeatNumber(seatDto.getSeatNumber());

        SeatEntity updatedSeat = seatRepository.save(existingSeat);
        return seatMapper.entityToDto(updatedSeat);
    }

    @Override
    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new SeatNotFoundException("Seat not found with id: " + id);
        }
        seatRepository.deleteById(id);
    }
}
