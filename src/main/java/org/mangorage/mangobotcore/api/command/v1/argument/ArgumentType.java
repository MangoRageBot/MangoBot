package org.mangorage.mangobotcore.api.command.v1.argument;

import java.util.List;

public abstract class ArgumentType<T> {
    public abstract T parse(String[] input, int startIndex) throws ArgumentParseException;

    public String getArgumentTypeString() {
        return null;
    }

    public final String getString() {
        final var argumentTypeString = getArgumentTypeString();
        if (argumentTypeString == null || argumentTypeString.isEmpty())
            return getClass().getSimpleName();
        return getClass().getSimpleName() + "(" + getArgumentTypeString() + ")";
    }

    public List<String> getSuggestions() {
        return List.of("None");
    }
}
