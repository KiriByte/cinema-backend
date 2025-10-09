package org.kiribyte.hallservice.mapper;

import org.kiribyte.hallservice.dto.AddHallRequest;
import org.kiribyte.hallservice.dto.HallDto;
import org.kiribyte.hallservice.dto.HallWithSeatsDto;
import org.kiribyte.hallservice.entity.HallEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = SeatMapper.class)
public interface HallMapper {

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seats", ignore = true)
    HallEntity addRequestToEntity(AddHallRequest request);

    // Entity -> DTO
    HallDto entityToDto(HallEntity entity);

    // DTO -> Entity
    @Mapping(target = "seats", ignore = true)
    HallEntity dtoToEntity(HallDto dto);

    // Entity -> DTO with seats
    //@Mapping(target = "seats", source = "seats")
    HallWithSeatsDto entityToDtoWithSeats(HallEntity entity);

    // Update methods
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seats", ignore = true)
    void updateEntityFromDto(HallDto dto, @MappingTarget HallEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seats", ignore = true)
    void updateEntityFromRequest(AddHallRequest request, @MappingTarget HallEntity entity);

    // List mappings
    List<HallDto> entitiesToDtos(List<HallEntity> entities);

    List<HallEntity> dtosToEntities(List<HallDto> dtos);

    List<HallWithSeatsDto> entitiesToDtosWithSeats(List<HallEntity> entities);
}
