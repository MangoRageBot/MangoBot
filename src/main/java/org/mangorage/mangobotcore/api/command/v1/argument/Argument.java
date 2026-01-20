package org.mangorage.mangobotcore.api.command.v1.argument;

import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;

import java.util.Arrays;

public abstract class Argument<T> {
    private final String name;
    private final String description;
    private final ArgumentType<T> type;
    private final int argumentIndex;

    public Argument(String name, String description, int argumentIndex, ArgumentType<T> argumentType) {
        this.name = name;
        this.description = description;
        this.type = argumentType;
        this.argumentIndex = argumentIndex;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    public T get(String[] input, CommandParseResult parseResult) {
        try {
            return type.parse(input, argumentIndex);
        } catch (ArgumentParseException e) {
            parseResult.addMessage(
                    "Argument parsing failed for argument '" + name + "'."
            );
            parseResult.addMessage(
                    "Expected type: " + type.getString() + ", but got input: '" + Arrays.toString(input) + "'"
            );
            return null;
        }
    }
}
