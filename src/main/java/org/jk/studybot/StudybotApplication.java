package org.jk.studybot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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

        JDABuilder.createDefault(token)
                .setActivity(Activity.playing("명령어 : !!명령어"))
                .setMaxReconnectDelay(32)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(
                        context.getBean(StudyBotDiscordListener.class),
                        context.getBean(VoiceChannelTracker.class)
                )
                .build();
    }

}
