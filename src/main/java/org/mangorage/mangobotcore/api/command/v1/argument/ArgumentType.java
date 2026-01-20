package org.mangorage.mangobotcore.api.command.v1.argument;

public abstract class ArgumentType<T> {
    public abstract T parse(String[] input, int startIndex) throws ArgumentParseException;
    public abstract String getString();
}
