package org.jk.studybot.command.text;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatusCommand implements TextCommands {
    @Override
    public String getName() {
        return "상태";
    }

    @Override
    public String getDescription() {
        return "봇의 현재 상태를 확인합니다";
    }

    @Override
    public String execute(String displayName, String userName) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("🤖 StudyBot 상태\n• 현재 시간: %s\n• 상태: 정상 작동중\n• 요청자: %s", currentTime, displayName);
    }
}