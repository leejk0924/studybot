package org.jk.studybot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DiscordBotToken {
    @Value("${discord.bot.token}")
    private String token;

    @Value("${discord.guild.id}")
    private String guildId;
}
