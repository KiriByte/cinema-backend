package org.kiribyte.sessionservice.service;

import org.kiribyte.sessionservice.dto.CreateSessionRequest;
import org.kiribyte.sessionservice.dto.SessionResponse;
import org.kiribyte.sessionservice.entity.SessionStatus;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    List<SessionResponse> getAllSessions();
    List<SessionResponse> getAllActiveSessions();

    SessionResponse getById(UUID id);

    SessionResponse addSession(CreateSessionRequest request);

    SessionResponse updateSession(UUID id, CreateSessionRequest session);

    void deleteSession(UUID id);

    void updateSessionStatus(UUID id, SessionStatus status);
}
