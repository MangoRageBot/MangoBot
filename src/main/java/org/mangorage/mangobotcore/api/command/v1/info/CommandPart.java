package org.mangorage.mangobotcore.api.command.v1.info;

import java.util.List;

public final class CommandPart {
    private final String name;
    private final List<String> aliases;
    private final List<String> commandNotes;
    private final List<ParameterPart> parameters;

    public CommandPart(String name, List<String> aliases, List<String> commandNotes, List<ParameterPart> parameters) {
        this.name = name;
        this.aliases = aliases;
        this.commandNotes = commandNotes;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<String> getCommandNotes() {
        return commandNotes;
    }

    public List<ParameterPart> getParameters() {
        return parameters;
    }
}
