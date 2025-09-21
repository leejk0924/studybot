package org.jk.studybot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voice_channel_logs")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class VoiceChannelLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Getter
    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "channel_id")
    private Long channelId;

    @Getter
    @Column(name = "channel_name")
    private String channelName;

    @Getter
    @Column(name = "duration")
    private Long duration;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "user_name")
    private String userName;

    @Getter
    @Column(name = "current_session_start")
    private LocalDateTime currentSessionStart;

    @Getter
    @Column(name = "is_currently_studying")
    private boolean isCurrentlyStudying;

    public VoiceChannelLog(Long userId, String nickName, Long channelId, String channelName, Long duration, LocalDateTime recordedAt, String userName) {
        this.userId = userId;
        this.nickName = nickName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.duration = duration;
        this.recordedAt = recordedAt;
        this.userName = userName;
        this.isCurrentlyStudying = false;
    }

    public void startStudySession(LocalDateTime sessionStart) {
        if (!isCurrentlyStudying) {
            this.currentSessionStart = sessionStart;
            this.isCurrentlyStudying = true;
        }
    }

    public void endStudySession(LocalDateTime sessionEnd) {
        if (isCurrentlyStudying && currentSessionStart != null) {
            long sessionDuration = java.time.Duration.between(currentSessionStart, sessionEnd).getSeconds();
            this.duration += sessionDuration;
            this.isCurrentlyStudying = false;
            this.recordedAt = sessionEnd;
        }
    }

    public long getCurrentSessionDuration() {
        if (isCurrentlyStudying && currentSessionStart != null) {
            return java.time.Duration.between(currentSessionStart, LocalDateTime.now()).getSeconds();
        }
        return 0;
    }

}
