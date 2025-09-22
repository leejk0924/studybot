package org.jk.studybot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jk.studybot.bot.DiscordBotToken;
import org.jk.studybot.listener.StudyBotDiscordListener;
import org.jk.studybot.listener.VoiceChannelTracker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StudybotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(StudybotApplication.class, args);
        DiscordBotToken discordBotTokenEntity = context.getBean(DiscordBotToken.class);
        String token = discordBotTokenEntity.getToken();
        String guildId = discordBotTokenEntity.getGuildId();

        var jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("명령어 : !!명령어"))
                .setMaxReconnectDelay(32)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(
                        context.getBean(StudyBotDiscordListener.class),
                        context.getBean(VoiceChannelTracker.class)
                )
                .build();

        jda.addEventListener(new net.dv8tion.jda.api.hooks.ListenerAdapter() {
            @Override
            public void onReady(ReadyEvent event) {
                var guild = jda.getGuildById(guildId);

                if (guild != null) {
                    guild.updateCommands().addCommands(
                        net.dv8tion.jda.api.interactions.commands.build.Commands.slash("명령어", "사용 가능한 명령어 목록을 보여줍니다")
                    ).queue(
                        success -> System.out.println("Guild Slash Command 등록 성공: " + guild.getName()),
                        error -> System.err.println("Guild Slash Command 등록 실패: " + error.getMessage())
                    );
                } else {
                    System.err.println("Guild를 찾을 수 없습니다: " + guildId);
                }
            }
        });
    }
}
