package org.mangorage.mangobotcore.api.command.v1.argument.types;

import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;

public final class IntegerArgumentType extends ArgumentType<Integer> {
    public static final ArgumentType<Integer> INSTANCE = new IntegerArgumentType();

    IntegerArgumentType() {}

    @Override
    public Integer parse(String input) throws IllegalArgumentException {
        return Integer.parseInt(input);
    }
}
