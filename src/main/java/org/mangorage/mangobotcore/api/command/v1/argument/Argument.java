package org.mangorage.mangobotcore.api.command.v1.argument;

public abstract class Argument<T> {
    private final String name;
    private final String description;
    private final ArgumentType<T> type;

    public Argument(String name, String description, ArgumentType<T> argumentType) {
        this.name = name;
        this.description = description;
        this.type = argumentType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    public T parse(String input) throws IllegalArgumentException {
        return type.parse(input);
    }
}
