package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.command.v1.argument.Argument;

public final class CommandContext<C> {

    public static <C> CommandContext<C> of(String[] arguments, CommandParseResult commandParseResult) {
        return new CommandContext<>(arguments, commandParseResult);
    }

    private final CommandParseResult commandParseResult;
    private final String[] arguments;
    private int argumentIndex = 0;

    private CommandContext(String[] arguments, CommandParseResult commandParseResult) {
        this.arguments = arguments;
        this.commandParseResult = commandParseResult;
    }

    public String[] getArguments() {
        return arguments;
    }

    public CommandParseResult getParseResult() {
        return commandParseResult;
    }

    public <T> T getArgument(Argument<T> argument) {
        final var rawValue = argument.get(arguments, argumentIndex, commandParseResult);
        if (rawValue.argumentConsumed())
            argumentIndex++;
        return rawValue.value();
    }
}
