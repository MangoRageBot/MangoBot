package org.mangorage.mangobotcore.api.command.v1.argument;

import org.mangorage.mangobotcore.api.command.v1.ArgumentResult;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import java.util.Arrays;

public abstract class Argument<T> {
    private final String name;
    private final String description;
    private final ArgumentType<T> type;

    public Argument(String name, String description, ArgumentType<T> argumentType) {
        this.name = name;
        this.description = description;
        this.type = argumentType;
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

    public ArgumentResult<T> get(String[] input, int argumentIndex, CommandParseResult parseResult) {
        try {
            return new ArgumentResult<>(type.parse(input, argumentIndex), true);
        } catch (ArgumentParseException e) {
            parseResult.addMessage(
                    "Argument parsing failed for argument '" + name + "'."
            );
            parseResult.addMessage(
                    "Expected type: " + type.getString() + ", but got input: '" + Arrays.toString(input) + "'"
            );
            return ArgumentResult.empty();
        }
    }

    public String getString() {
        return getClass().getSimpleName() + "(" + type.getString() + ")";
    }
}
