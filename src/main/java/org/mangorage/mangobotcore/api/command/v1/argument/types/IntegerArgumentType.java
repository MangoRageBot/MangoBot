package org.mangorage.mangobotcore.api.command.v1.argument.types;

import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentParseException;
import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;

public final class IntegerArgumentType extends ArgumentType<Integer> {
    public static final IntegerArgumentType INSTANCE = new IntegerArgumentType();

    IntegerArgumentType() {}

    @Override
    public Integer parse(String[] input, int argumentIndex) throws ArgumentParseException {
        return Integer.parseInt(input[argumentIndex]);
    }

    @Override
    public String getString() {
        return getClass().getSimpleName();
    }
}
