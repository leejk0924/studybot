package org.jk.studybot.repository;

import org.jk.studybot.entity.VoiceChannelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VoiceChannelLogRepository extends JpaRepository<VoiceChannelLog, Long> {

    @Query("SELECT log FROM VoiceChannelLog log WHERE log.recordedAt >= :start AND log.recordedAt < :end")
    List<VoiceChannelLog> findAllLogsBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * FROM voice_channel_logs WHERE user_name = :userName AND DATE(recorded_at) = CURRENT_DATE ORDER BY recorded_at DESC LIMIT 1", nativeQuery = true)
    VoiceChannelLog findTodayLogByUserName(String userName);

    List<VoiceChannelLog> findByUserName(String userName);

    List<VoiceChannelLog> findByUserNameAndRecordedAtBetween(String userName, LocalDateTime start, LocalDateTime end);

    @Query("SELECT log FROM VoiceChannelLog log WHERE YEAR(log.recordedAt) = :year AND MONTH(log.recordedAt) = :month")
    List<VoiceChannelLog> findByYearAndMonth(int year, int month);

    @Query("SELECT log FROM VoiceChannelLog log WHERE log.recordedAt >= :startOfWeek AND log.recordedAt < :endOfWeek")
    List<VoiceChannelLog> findByWeek(LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    @Query("SELECT log FROM VoiceChannelLog log WHERE log.userName = :userName AND log.isCurrentlyStudying = true")
    VoiceChannelLog findActiveSessionByUserName(String userName);

    @Query("SELECT log FROM VoiceChannelLog log WHERE log.isCurrentlyStudying = true")
    List<VoiceChannelLog> findAllActiveSessions();

    @Modifying
    @Query("UPDATE VoiceChannelLog log SET log.isCurrentlyStudying = false WHERE log.isCurrentlyStudying = true")
    int closeAllActiveSessions();
}
