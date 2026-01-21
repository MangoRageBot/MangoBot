package org.mangorage.mangobotcore.api.command.v1.argument;

import org.mangorage.mangobotcore.api.command.v1.ArgumentResult;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;

import java.util.Optional;


public final class RequiredArg<T> extends Argument<T> {
    public RequiredArg(String name, String description, ArgumentType<T> type) {
        super(name, description, type);
    }

    @Override
    public ArgumentResult<T> get(String[] input, int argumentIndex, CommandParseResult parseResult) {
        final var argument = super.get(input, argumentIndex, parseResult);
        if (!argument.isPresent()) {
            throw new ArgumentParseException("Required Arg not found: " + getName());
        }
        return argument;
    }
}
