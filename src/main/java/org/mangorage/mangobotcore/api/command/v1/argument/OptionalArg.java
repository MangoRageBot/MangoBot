package org.mangorage.mangobotcore.api.command.v1.argument;

public class OptionalArg<T> extends Argument<T> {
    private final T defaultValue;

    public OptionalArg(String name, String description, ArgumentType<T> argumentType, T defaultValue) {
        super(name, description, argumentType);
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
