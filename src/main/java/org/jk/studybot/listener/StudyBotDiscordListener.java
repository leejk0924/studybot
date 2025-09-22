package org.jk.studybot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jk.studybot.command.CommandHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyBotDiscordListener extends ListenerAdapter {
    private final CommandHandler commandHandler;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        Message message = event.getMessage();
        String userName = user.getEffectiveName();

        if (user.isBot()) {
            return;
        }

        if (!event.getChannel().getType().isMessage()) {
            return;
        }

        if (event.getChannel().getType().isThread()) {
            return;
        }

        TextChannel textChannel = event.getChannel().asTextChannel();

        String content = message.getContentDisplay().trim();
        if (content.startsWith("!!")) {
            String cmd = content.substring(2).trim();
            if (cmd.equals("명령어")) {
                message.delete().queueAfter(15, TimeUnit.SECONDS,
                    success -> {},
                    error -> log.warn("Failed to delete message: {}", error.getMessage()));
                textChannel.sendMessage("명령어를 선택하고나 취소할 수 있습니다.")
                        .addActionRow(commandHandler.getAllCommandsDropdown())
                        .addActionRow(Button.danger("cancel_menu", "취소"))
                        .queue(m -> m.delete().queueAfter(50, TimeUnit.SECONDS,
                            success -> {},
                            error -> log.warn("Failed to delete message: {}", error.getMessage())));
                return;
            }
            var returnMessage = commandHandler.handle(cmd, userName, user.getName());
            message.delete().queueAfter(5, TimeUnit.SECONDS,
                success -> {},
                error -> log.warn("Failed to delete message: {}", error.getMessage()));
            textChannel.sendMessage(returnMessage).queue(m -> {
                m.delete().queueAfter(5, TimeUnit.SECONDS,
                    success -> {},
                    error -> log.warn("Failed to delete message: {}", error.getMessage()));
            });
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        var componentId = event.getComponentId();
        String userName = event.getUser().getEffectiveName();
        if (componentId.startsWith("command_selector")) {
            var selected = event.getValues().get(0);
            var returnMessage = commandHandler.handle(selected, userName, event.getUser().getName());

            event.reply(returnMessage).setEphemeral(true).queue(hook -> {
                hook.deleteOriginal().queueAfter(15, TimeUnit.SECONDS);
            });

            if (!event.getMessage().getFlags().contains(Message.MessageFlag.EPHEMERAL)) {
                event.getMessage().delete().queue(
                    success -> {},
                    error -> log.warn("Failed to delete message: {}", error.getMessage()));
            }
        }
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("명령어")) {
            event.reply("명령어를 선택하거나 취소할 수 있습니다.")
                    .addActionRow(commandHandler.getAllCommandsDropdown())
                    .addActionRow(Button.danger("cancel_menu", "취소"))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("cancel_menu")) {
            event.reply("명령어 선택이 취소되었습니다.").setEphemeral(true).queue();
            event.getMessage().delete().queue(
                success -> {},
                error -> log.warn("Failed to delete message: {}", error.getMessage()));
        }
    }
}
