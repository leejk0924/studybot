package org.jk.studybot.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jk.studybot.service.DailySummaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogScheduler {
    private final DailySummaryService dailySummaryService;

    @Scheduled(cron = "0 1 0 * * *")
    public void sendDailySummary() {
        dailySummaryService.generateAndSendDailySummary();
    }
}
