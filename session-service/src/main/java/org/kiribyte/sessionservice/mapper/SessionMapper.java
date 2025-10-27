package org.kiribyte.sessionservice.mapper;

import org.kiribyte.sessionservice.dto.CreateSessionRequest;
import org.kiribyte.sessionservice.dto.SessionResponse;
import org.kiribyte.sessionservice.entity.SessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SessionEntity toEntity(CreateSessionRequest request);

    SessionResponse toResponse(SessionEntity entity);

}
