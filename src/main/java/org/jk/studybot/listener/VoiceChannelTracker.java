package org.jk.studybot.listener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jk.studybot.config.TextChannelProperties;
import org.jk.studybot.config.VoiceChannelProperties;
import org.jk.studybot.entity.VoiceChannelLog;
import org.jk.studybot.repository.VoiceChannelLogRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VoiceChannelTracker extends ListenerAdapter {
    private final VoiceChannelLogRepository repository;
    private final VoiceChannelProperties voiceChannelProperties;
    private final TextChannelProperties textChannelProperties;

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        var member = event.getEntity();

        var userId = member.getIdLong();
        var nickname = member.getEffectiveName();
        var joinedChannel = event.getChannelJoined();
        var leftChannel = event.getChannelLeft();
        var user = member.getUser();

        var nickName = nickname != null ? nickname : user.getName();

        var targetVoiceChannelName = voiceChannelProperties.getTargetChannelName();
        var targetTextChannelName = textChannelProperties.getTargetChannelName();

        var textChannels = event.getGuild().getTextChannelsByName(targetTextChannelName, true);
        TextChannel textChannel = textChannels != null && !textChannels.isEmpty() ? textChannels.get(0) : null;

        if (joinedChannel != null && joinedChannel.getName().equals(targetVoiceChannelName)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
            VoiceChannelLog activeSession = repository.findActiveSessionByUserName(user.getName(), todayStart);
            if (activeSession == null) {

                VoiceChannelLog todayLog = repository.findTodayLogByUserName(user.getName());
                if (todayLog == null) {
                    todayLog = new VoiceChannelLog(
                            userId,
                            nickName,
                            joinedChannel.getIdLong(),
                            joinedChannel.getName(),
                            0L,
                            now,
                            user.getName()
                    );
                }
                todayLog.startStudySession(now);
                repository.save(todayLog);

                if (textChannel != null) {
                    textChannel
                            .sendMessage(nickName + "님이 `" + joinedChannel.getName() + "` 채널에 입장했습니다.")
                            .queue();
                }
            }
        }
        if (leftChannel != null && leftChannel.getName().equals(targetVoiceChannelName)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
            VoiceChannelLog activeSession = repository.findActiveSessionByUserName(user.getName(), todayStart);
            if (activeSession != null) {
                activeSession.endStudySession(now);
                repository.save(activeSession);

                long totalDuration = activeSession.getDuration();
                long hours = totalDuration / 3600;
                long minutes = (totalDuration % 3600) / 60;
                long seconds = totalDuration % 60;

                if (textChannel != null) {
                    textChannel.sendMessage(
                            nickName + "님이 `" + leftChannel.getName() + "` 채널에서 퇴장했습니다.\n" +
                                    "오늘 총 공부시간: " +
                                    (hours > 0 ? hours + "시간 " : "") +
                                    (minutes > 0 ? minutes + "분 " : "") +
                                    seconds + "초"
                    ).queue();
                }
            }
        }
    }
}
