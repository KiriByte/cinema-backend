package org.kiribyte.hallservice.service.impl;

import org.kiribyte.hallservice.dto.AddSeatTypeRequest;
import org.kiribyte.hallservice.dto.SeatTypeDto;
import org.kiribyte.hallservice.entity.SeatEntity;
import org.kiribyte.hallservice.entity.SeatTypeEntity;
import org.kiribyte.hallservice.exception.SeatTypeAlreadyExistsException;
import org.kiribyte.hallservice.exception.SeatTypeInUseException;
import org.kiribyte.hallservice.exception.SeatTypeNotFoundException;
import org.kiribyte.hallservice.mapper.SeatTypeMapper;
import org.kiribyte.hallservice.repository.SeatRepository;
import org.kiribyte.hallservice.repository.SeatTypeRepository;
import org.kiribyte.hallservice.service.SeatTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatTypeServiceImpl implements SeatTypeService {

    private final SeatTypeRepository seatTypeRepository;
    private final SeatTypeMapper seatTypeMapper;
    private final SeatRepository seatRepository;

    public SeatTypeServiceImpl(SeatTypeRepository seatTypeRepository, SeatTypeMapper seatTypeMapper, SeatRepository seatRepository) {
        this.seatTypeRepository = seatTypeRepository;
        this.seatTypeMapper = seatTypeMapper;
        this.seatRepository = seatRepository;
    }

    @Override
    public SeatTypeDto getSeatTypeById(Long id) {
        SeatTypeEntity entity = seatTypeRepository.findById(id)
                .orElseThrow(() -> new SeatTypeNotFoundException("Seat Type not found with id: " + id));
        return seatTypeMapper.toDto(entity);
    }

    @Override
    public List<SeatTypeDto> getAllSeatType() {
        List<SeatTypeEntity> entities = seatTypeRepository.findAll();
        List<SeatTypeDto> seatTypeDtos = new ArrayList<>();
        for (SeatTypeEntity entity : entities) {
            seatTypeDtos.add(seatTypeMapper.toDto(entity));
        }
        return seatTypeDtos;
    }

    @Override
    public SeatTypeDto addSeatType(AddSeatTypeRequest request) {
        if (seatTypeRepository.existsByName(request.getName())) {
            throw new SeatTypeAlreadyExistsException("Seat type with name '" + request.getName() + "' already exists");
        }

        SeatTypeEntity entity = seatTypeMapper.toEntity(request);
        SeatTypeEntity savedEntity = seatTypeRepository.save(entity);
        return seatTypeMapper.toDto(savedEntity);
    }

    @Override
    public SeatTypeDto updateSeatType(Long id, SeatTypeDto seatTypeDto) {
        SeatTypeEntity existingSeatType = seatTypeRepository.findById(id)
                .orElseThrow(() -> new SeatTypeNotFoundException("Seat Type not found with id: " + id));
        if (!existingSeatType.getName().equals(seatTypeDto.getName()) &&
                seatTypeRepository.existsByName(seatTypeDto.getName())) {
            throw new SeatTypeAlreadyExistsException("Seat type with name '" + seatTypeDto.getName() + "' already exists");
        }
        existingSeatType.setName(seatTypeDto.getName());
        existingSeatType.setDescription(seatTypeDto.getDescription());
        SeatTypeEntity updatedSeatType = seatTypeRepository.save(existingSeatType);
        return seatTypeMapper.toDto(updatedSeatType);


    }

    @Override
    public void deleteSeatType(Long id) {
        List<SeatEntity> seatsWithThisType = seatRepository.findBySeatTypeId(id);
        if (!seatsWithThisType.isEmpty()) {
            throw new SeatTypeInUseException("Cannot delete seat type. It is used by " + seatsWithThisType.size() + " seats.");
        }

        seatTypeRepository.deleteById(id);
    }
}
