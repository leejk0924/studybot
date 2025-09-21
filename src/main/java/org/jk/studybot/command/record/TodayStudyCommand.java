package org.jk.studybot.command.record;

import lombok.RequiredArgsConstructor;
import org.jk.studybot.repository.VoiceChannelLogRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TodayStudyCommand implements RecordCommands {
    private final VoiceChannelLogRepository repository;

    @Override
    public String getName() {
        return "오늘의 공부시간";
    }

    @Override
    public String getDescription() {
        return "오늘의 공부시간을 조회합니다";
    }

    @Override
    public String execute(String displayName, String userName) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        var logs = repository.findByUserNameAndRecordedAtBetween(userName, startOfDay, endOfDay);
        if (logs.isEmpty()) {
            return displayName + "님의 오늘 공부 기록이 없습니다.";
        }

        long totalSeconds = logs.stream()
                .mapToLong(log -> log.getDuration())
                .sum();

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("📅 %s님의 오늘 공부시간: %d시간 %d분 %d초", displayName, hours, minutes, seconds);
    }
}