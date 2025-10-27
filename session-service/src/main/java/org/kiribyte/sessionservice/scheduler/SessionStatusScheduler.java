package org.kiribyte.sessionservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kiribyte.sessionservice.entity.SessionEntity;
import org.kiribyte.sessionservice.entity.SessionStatus;
import org.kiribyte.sessionservice.repostiory.SessionRepository;
import org.kiribyte.sessionservice.service.SessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionStatusScheduler {

    private final SessionRepository sessionRepository;
    private final SessionService sessionService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateSessionStatuses() {
        log.info("Checking sessions to complete");
        Instant currentTime = Instant.now();
        log.info("Current time (UTC): {}", currentTime);
        
        try {
            List<SessionEntity> sessionsToStart = sessionRepository.findSessionsToStart(currentTime);

            for (SessionEntity session : sessionsToStart) {
                sessionService.updateSessionStatus(session.getId(), SessionStatus.COMPLETED);
                log.info("Completed session {} ", session.getId());
            }

            if (!sessionsToStart.isEmpty()) {
                log.info("Completed {} sessions", sessionsToStart.size());
            }

        } catch (Exception e) {
            log.error("Error updating session statuses", e);
        }
    }
}
