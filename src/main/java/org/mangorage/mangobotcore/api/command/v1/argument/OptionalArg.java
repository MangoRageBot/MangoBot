package org.mangorage.mangobotcore.api.command.v1.argument;

public final class OptionalArg<T> extends Argument<T> {
    public OptionalArg(String name, String description, ArgumentType<T> argumentType) {
        super(name, description, argumentType);
    }
}
