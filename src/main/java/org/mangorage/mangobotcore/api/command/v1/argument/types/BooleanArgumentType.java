package org.mangorage.mangobotcore.api.command.v1.argument.types;

import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentParseException;
import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;

public final class BooleanArgumentType extends ArgumentType<Boolean> {
    public static final BooleanArgumentType INSTANCE = new BooleanArgumentType();

    private BooleanArgumentType() {}

    @Override
    public Boolean parse(String[] input, int startIndex) throws ArgumentParseException {
        return Boolean.parseBoolean(input[startIndex]);
    }

    @Override
    public String getString() {
        return Boolean.class.getSimpleName();
    }
}
