package org.jk.studybot.command;

public interface Commands {
    String getName();

    String getDescription();

    String execute(String displayName, String userName);
}
