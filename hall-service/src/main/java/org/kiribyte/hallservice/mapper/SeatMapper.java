package org.kiribyte.hallservice.mapper;

import org.kiribyte.hallservice.dto.SeatDto;
import org.kiribyte.hallservice.entity.SeatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "hallId", source = "hallId.id")
    @Mapping(target = "seatType", source = "seatType.name")
    @Mapping(target = "seatTypeId", source = "seatType.id")
    SeatDto entityToDto(SeatEntity entity);

    @Mapping(target = "hallId", ignore = true)
    @Mapping(target = "seatType", ignore = true)
    SeatEntity dtoToEntity(SeatDto dto);

    List<SeatDto> entitiesToDtos(List<SeatEntity> entities);

    List<SeatEntity> dtosToEntities(List<SeatDto> dtos);
}
