package org.kiribyte.hallservice.mapper;

import org.kiribyte.hallservice.dto.AddSeatTypeRequest;
import org.kiribyte.hallservice.dto.SeatTypeDto;
import org.kiribyte.hallservice.entity.SeatTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatTypeMapper {

    // Entity -> DTO
    SeatTypeDto toDto(SeatTypeEntity entity);

    // DTO -> Entity
    SeatTypeEntity toEntity(SeatTypeDto dto);

    // Request -> Entity
    SeatTypeEntity toEntity(AddSeatTypeRequest request);

    // Request -> DTO
    SeatTypeDto toDto(AddSeatTypeRequest request);
}
