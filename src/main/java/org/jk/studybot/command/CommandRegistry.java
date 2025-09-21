package org.jk.studybot.command;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandRegistry {
    private final List<Commands> commandList;
    private final Map<String, Commands> commandMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Commands command : commandList) {
            commandMap.put(command.getName(), command);
        }
    }
    public Commands getCommand(String name) {
        return commandMap.get(name);
    }

    public List<Commands> getAllCommands() {
        return commandList;
    }
}
