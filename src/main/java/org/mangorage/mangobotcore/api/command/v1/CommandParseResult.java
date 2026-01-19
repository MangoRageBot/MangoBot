package org.mangorage.mangobotcore.api.command.v1;

import java.util.ArrayList;
import java.util.List;

public final class CommandParseResult {
    private final List<String> messages = new ArrayList<>();

    public CommandParseResult() {}

    public void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return List.copyOf(messages);
    }
}
