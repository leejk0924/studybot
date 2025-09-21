package org.jk.studybot.command.record;

import lombok.RequiredArgsConstructor;
import org.jk.studybot.repository.VoiceChannelLogRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyTimeCommand implements RecordCommands {
    private final VoiceChannelLogRepository repository;

    @Override
    public String getName() {
        return "총 공부시간";
    }

    @Override
    public String getDescription() {
        return "나의 총 공부시간을 조회합니다";
    }

    @Override
    public String execute(String displayName, String userName) {

        var logs = repository.findByUserName(userName);
        if (logs.isEmpty()) {
            return displayName + "님의 공부 기록이 없습니다.";
        }

        long totalSeconds = logs.stream()
                .mapToLong(log -> log.getDuration())
                .sum();

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("📚 %s님의 총 공부시간: %d시간 %d분 %d초",
                displayName, hours, minutes, seconds);
    }
}