package org.mangorage.mangobotcore.api.command.v1.argument;

import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;


public final class RequiredArg<T> extends Argument<T> {
    public RequiredArg(String name, String description, int argumentIndex, ArgumentType<T> type) {
        super(name, description, argumentIndex, type);
    }

    @Override
    public T get(String[] input, CommandParseResult parseResult) {
        final var argument = super.get(input, parseResult);
        if (argument == null) {
            throw new ArgumentParseException("Required Arg not found: " + getName());
        }
        return argument;
    }
}
