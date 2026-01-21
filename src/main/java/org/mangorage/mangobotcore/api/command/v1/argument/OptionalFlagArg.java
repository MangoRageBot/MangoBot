package org.mangorage.mangobotcore.api.command.v1.argument;

import org.mangorage.mangobotcore.api.command.v1.ArgumentResult;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.argument.types.BooleanArgumentType;


public final class OptionalFlagArg extends OptionalArg<Boolean> {
    public OptionalFlagArg(String name, String description) {
        super(name, description, BooleanArgumentType.INSTANCE);
    }

    @Override
    public ArgumentResult<Boolean> get(String[] input, int argumentIndex, CommandParseResult parseResult) {
        final var value = input[argumentIndex] != null && input[argumentIndex].equalsIgnoreCase(getName());
        if (value) return new ArgumentResult<>(true, true);
        return new ArgumentResult<>(false, false);
    }
}
