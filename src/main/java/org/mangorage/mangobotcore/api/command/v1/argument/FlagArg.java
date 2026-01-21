package org.mangorage.mangobotcore.api.command.v1.argument;

import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.argument.types.BooleanArgumentType;

public final class FlagArg extends Argument<Boolean> {
    public FlagArg(String name, String description, int argumentIndex) {
        super(name, description, argumentIndex, BooleanArgumentType.INSTANCE);
    }

    @Override
    public Boolean get(String[] input, CommandParseResult parseResult) {
        return input[getArgumentIndex()] != null && input[getArgumentIndex()].equalsIgnoreCase(getName());
    }
}
