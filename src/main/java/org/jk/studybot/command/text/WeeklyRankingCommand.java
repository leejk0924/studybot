package org.jk.studybot.command.text;

import lombok.RequiredArgsConstructor;
import org.jk.studybot.service.DailySummaryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyRankingCommand implements TextCommands {
    private final DailySummaryService dailySummaryService;

    @Override
    public String getName() {
        return "랭킹";
    }

    @Override
    public String getDescription() {
        return "저번주 스터디 시간 랭킹 1~3등을 확인합니다";
    }

    @Override
    public String execute(String displayName, String userName) {
        return dailySummaryService.getLastWeekRanking();
    }
}