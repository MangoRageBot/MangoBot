package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.command.v1.argument.Argument;

public final class CommandContext<C> {

    public static <C> CommandContext<C> of(C contextObject, String[] arguments, CommandParseResult commandParseResult) {
        return new CommandContext<>(contextObject, arguments, commandParseResult);
    }


    private final C contextObject;
    private final String[] arguments;
    private final CommandParseResult commandParseResult;

    private int index = 0;

    private CommandContext(C contextObject, String[] arguments, CommandParseResult commandParseResult) {
        this.contextObject = contextObject;
        this.arguments = arguments;
        this.commandParseResult = commandParseResult;
    }

    public C getContextObject() {
        return contextObject;
    }

    public String[] getArguments() {
        return arguments;
    }

    public CommandParseResult getParseResult() {
        return commandParseResult;
    }

    public <T> T getArgument(Argument<T> argument) {
        final var rawValue = argument.get(arguments, index, commandParseResult);
        if (rawValue.argumentConsumed())
            index++;
        return rawValue.value();
    }

    public String peek() {
        return index < arguments.length ? arguments[index] : null;
    }

    public String next() {
        return index < arguments.length ? arguments[index++] : null;
    }

    public int remaining() {
        return arguments.length - index;
    }
}
