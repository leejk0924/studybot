package org.jk.studybot.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jk.studybot.repository.VoiceChannelLogRepository;
import org.jk.studybot.service.DailySummaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogScheduler {
    private final DailySummaryService dailySummaryService;
    private final VoiceChannelLogRepository voiceChannelLogRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupActiveSessions() {
        log.info("Starting midnight session cleanup...");
        int closedSessions = voiceChannelLogRepository.closeAllActiveSessions();
        log.info("Closed {} active sessions at midnight", closedSessions);
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void sendDailySummary() {
        dailySummaryService.generateAndSendDailySummary();
    }
}
