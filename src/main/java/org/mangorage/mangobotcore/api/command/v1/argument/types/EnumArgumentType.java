package org.mangorage.mangobotcore.api.command.v1.argument.types;

import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentParseException;
import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;

public final class EnumArgumentType<E extends Enum<E>> extends ArgumentType<E> {
    public static <E extends Enum<E>> EnumArgumentType<E> of(Class<E> enumClass) {
        return new EnumArgumentType<>(enumClass);
    }

    private final Class<E> enumClass;

    private EnumArgumentType(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E parse(String[] input, int startIndex) throws ArgumentParseException {
        return Enum.valueOf(enumClass, input[startIndex]);
    }

    @Override
    public String getString() {
        return enumClass.getSimpleName();
    }
}
