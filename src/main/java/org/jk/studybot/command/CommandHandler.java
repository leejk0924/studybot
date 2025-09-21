package org.jk.studybot.command;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final CommandRegistry registry;

    public String handle(String commandName, String displayName, String userName) {
        Commands command = registry.getCommand(commandName);
        if (command == null) {
            return "잘못된 명령어입니다.";
        }
        return command.execute(displayName, userName);
    }

    public StringSelectMenu getAllCommandsDropdown() {
        var menu = StringSelectMenu.create("command_selector_all")
                .setPlaceholder("명령어를 선택하세요.");
        var allCommands = registry.getAllCommands();
        if (allCommands.isEmpty()) {
            menu.addOption("사용 가능한 명령어 없음", "no_commands", "현재 등록된 기록 명령어가 없습니다.");
        } else {
            allCommands.forEach(
                    cmd -> menu.addOption(cmd.getName(), cmd.getName(), cmd.getDescription()));
        }
        return menu.build();
    }
}
