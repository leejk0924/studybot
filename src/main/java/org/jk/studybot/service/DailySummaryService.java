package org.jk.studybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jk.studybot.config.TextChannelProperties;
import org.jk.studybot.entity.VoiceChannelLog;
import org.jk.studybot.repository.VoiceChannelLogRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailySummaryService {
    private final VoiceChannelLogRepository voiceChannelLogRepository;
    private final JDA jda;
    private final TextChannelProperties textChannelProperties;

    public void generateAndSendDailySummary() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(7);

        LocalDateTime startOfWeekTime = startOfWeek.atStartOfDay();
        LocalDateTime endOfWeekTime = endOfWeek.atStartOfDay();

        TextChannel textChannel = findTextChannel(textChannelProperties.getTargetChannelName());
        if (textChannel == null) {
            log.info("채널을 찾을 수 없습니다.");
            return;
        }

        var logs = voiceChannelLogRepository.findByWeek(startOfWeekTime, endOfWeekTime);
        if (logs.isEmpty()) {
            textChannel.sendMessage("이번 주의 기록이 없습니다.").queue();
            return;
        }

        var summary = formatWeeklyRanking(logs, startOfWeek, endOfWeek.minusDays(1));
        textChannel.sendMessage(summary).queue();
    }

    private TextChannel findTextChannel(String channelName) {
        return jda.getTextChannelsByName(channelName, true).stream().findFirst().orElse(null);
    }

    private String formatWeeklyRanking(List<VoiceChannelLog> logs, LocalDate startOfWeek, LocalDate endOfWeek) {
        Map<String, Long> userTotalDurations = new HashMap<>();

        for (var log : logs) {
            userTotalDurations.merge(log.getNickName(), log.getDuration(), Long::sum);
        }

        List<Map.Entry<String, Long>> ranking = userTotalDurations.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        StringBuilder response = new StringBuilder(String.format("%s ~ %s 주간 스터디 랭킹 🏆\n\n",
                startOfWeek.toString(), endOfWeek.toString()));

        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < ranking.size(); i++) {
            var entry = ranking.get(i);
            String formattedDuration = formatDuration(entry.getValue());
            response.append(String.format("%s %d등: %s님 - %s\n",
                    medals[i], i + 1, entry.getKey(), formattedDuration));
        }

        return response.toString();
    }

    public String getLastWeekRanking() {
        LocalDate now = LocalDate.now();
        LocalDate lastWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate lastWeekEnd = lastWeekStart.plusDays(7);

        LocalDateTime startOfLastWeek = lastWeekStart.atStartOfDay();
        LocalDateTime endOfLastWeek = lastWeekEnd.atStartOfDay();

        var logs = voiceChannelLogRepository.findByWeek(startOfLastWeek, endOfLastWeek);
        if (logs.isEmpty()) {
            return "저번 주의 기록이 없습니다.";
        }

        return formatWeeklyRanking(logs, lastWeekStart, lastWeekEnd.minusDays(1));
    }

    private String formatDuration(Long seconds) {
        long hours = seconds / 3600;
        long minutes = seconds % 3600 / 60;
        long second = seconds % 60;
        return String.format("%d시간 %d분 %d초", hours, minutes, second);
    }
}
