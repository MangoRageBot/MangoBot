package org.mangorage.mangobotcore.api.command.v1.argument.types;

import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentParseException;
import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;

public final class StringArgumentType extends ArgumentType<String> {

    public enum Type {
        QUOTE,
        SINGLE
    }

    public static StringArgumentType single() {
        return new StringArgumentType(Type.SINGLE);
    }

    public static StringArgumentType quote() {
        return new StringArgumentType(Type.QUOTE);
    }

    private final Type type;

    private StringArgumentType(Type type) {
        this.type = type;
    }

    @Override
    public String parse(String[] input, int startIndex) throws ArgumentParseException {
        if (startIndex >= input.length) {
            throw new ArgumentParseException("No input at index " + startIndex);
        }

        if (type == Type.SINGLE) {
            return input[startIndex];
        }

        // QUOTE
        String first = input[startIndex];
        if (!first.startsWith("\"")) {
            throw new ArgumentParseException("Expected quoted string at index " + startIndex);
        }

        StringBuilder builder = new StringBuilder();
        boolean closed = false;

        for (int i = startIndex; i < input.length; i++) {
            String part = input[i];

            if (i == startIndex) {
                part = part.substring(1); // remove opening quote
            }

            if (part.endsWith("\"")) {
                builder.append(part, 0, part.length() - 1);
                closed = true;
                break;
            }

            builder.append(part).append(" ");
        }

        if (!closed) {
            throw new ArgumentParseException("Unterminated quoted string");
        }

        return builder.toString();
    }

    @Override
    public String getArgumentTypeString() {
        return type.name();
    }
}