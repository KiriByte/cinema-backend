package org.kiribyte.sessionservice.controller;

import org.kiribyte.sessionservice.dto.CreateSessionRequest;
import org.kiribyte.sessionservice.dto.SessionResponse;
import org.kiribyte.sessionservice.service.SessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{id}")
    public SessionResponse getSession(@PathVariable UUID id) {
        return sessionService.getById(id);
    }

    @GetMapping("/")
    public List<SessionResponse> getSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/active")
    public List<SessionResponse> getActiveSessions() {
        return sessionService.getAllActiveSessions();
    }

    @PostMapping("/")
    public SessionResponse createSession(@RequestBody CreateSessionRequest request) {
        return sessionService.addSession(request);
    }

    @PutMapping("/{id}")
    public SessionResponse updateSession(@PathVariable UUID id, @RequestBody CreateSessionRequest request) {
        return sessionService.updateSession(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSession(@PathVariable UUID id) {
        sessionService.deleteSession(id);
    }

}
