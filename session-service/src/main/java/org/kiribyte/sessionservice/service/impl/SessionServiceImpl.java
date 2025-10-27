package org.kiribyte.sessionservice.service.impl;

import jakarta.transaction.Transactional;
import org.kiribyte.sessionservice.client.MovieClient;
import org.kiribyte.sessionservice.dto.CreateSessionRequest;
import org.kiribyte.sessionservice.dto.SessionResponse;
import org.kiribyte.sessionservice.entity.SessionEntity;
import org.kiribyte.sessionservice.entity.SessionStatus;
import org.kiribyte.sessionservice.exception.SessionNotFoundException;
import org.kiribyte.sessionservice.exception.SessionTimeConflictException;
import org.kiribyte.sessionservice.mapper.SessionMapper;
import org.kiribyte.sessionservice.repostiory.SessionRepository;
import org.kiribyte.sessionservice.service.SessionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final MovieClient movieClient;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              SessionMapper sessionMapper,
                              MovieClient movieClient) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.movieClient = movieClient;
    }

    @Override
    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }

    @Override
    public List<SessionResponse> getAllActiveSessions() {
        return sessionRepository.findActiveSessionsOrderByCreatedAtDesc()
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }

    @Override
    public SessionResponse getById(UUID id) {
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));
        return sessionMapper.toResponse(session);
    }

    @Transactional
    @Override
    public SessionResponse addSession(CreateSessionRequest request) {
        // Получаю фильм
        var movie = movieClient.getMovieById(request.getMovieId());

        // Рассчитываю время. Начало сеанса + длительность фильма + 30 минут на уборку
        var endTime = request.getStartTime().plusSeconds((movie.getDuration() + 30) * 60L);

        // Проверяю конфликт сессий
        validateNoTimeConflicts(request.getHallId(), request.getStartTime(), endTime, null);

        SessionEntity session = sessionMapper.toEntity(request);
        session.setEndTime(endTime);
        session.setStatus(SessionStatus.ACTIVE);

        SessionEntity savedSession = sessionRepository.save(session);
        return sessionMapper.toResponse(savedSession);
    }

    @Transactional
    @Override
    public SessionResponse updateSession(UUID id, CreateSessionRequest request) {
        SessionEntity existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with id: " + id));
        // Получаю фильм
        var movie = movieClient.getMovieById(request.getMovieId());

        // Рассчитываю время. Начало сеанса + длительность фильма + 30 минут на уборку
        var endTime = request.getStartTime().plusSeconds((movie.getDuration() + 30) * 60L);

        // Проверяю конфликт сессий
        validateNoTimeConflicts(request.getHallId(), request.getStartTime(), endTime, id);

        existingSession.setMovieId(request.getMovieId());
        existingSession.setHallId(request.getHallId());
        existingSession.setStartTime(request.getStartTime());
        existingSession.setPrice(request.getPrice());
        existingSession.setEndTime(endTime);

        SessionEntity savedSession = sessionRepository.save(existingSession);
        return sessionMapper.toResponse(savedSession);
    }

    @Transactional
    @Override
    public void deleteSession(UUID id) {
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with id: " + id));
        session.setStatus(SessionStatus.CANCELED);
        sessionRepository.save(session);
    }

    @Transactional
    @Override
    public void updateSessionStatus(UUID id, SessionStatus status) {
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with id: " + id));
        session.setStatus(status);
        sessionRepository.save(session);
    }

    private void validateNoTimeConflicts(Long hallId,
                                         Instant startTime,
                                         Instant endTime,
                                         UUID excludeSessionId) {
        var conflictingSessions = sessionRepository.findConflictingSessions(
                hallId, startTime, endTime, excludeSessionId);

        if (!conflictingSessions.isEmpty()) {
            var conflictSession = conflictingSessions.get(0);
            throw new SessionTimeConflictException(
                    String.format("Session time conflicts with existing session %s in hall %d. " +
                                    "Conflicting time: %s - %s",
                            conflictSession.getId(),
                            hallId,
                            conflictSession.getStartTime(),
                            conflictSession.getEndTime()));
        }
    }
}
