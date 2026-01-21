package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.command.v1.argument.Argument;

public final class CommandContext {

    public static CommandContext empty() {
        return new CommandContext(new String[]{});
    }

    public static CommandContext of(String[] arguments) {
        return new CommandContext(arguments);
    }

    private final String[] arguments;
    private int argumentIndex = 0;

    private CommandContext(String[] arguments) {
        this.arguments = arguments;
    }

    public <T> T getArgument(Argument<T> argument, CommandParseResult commandParseResult) {
        final var rawValue = argument.get(arguments, argumentIndex, commandParseResult);
        if (rawValue.argumentConsumed())
            argumentIndex++;
        return rawValue.value();
    }
}
