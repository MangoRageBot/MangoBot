package org.mangorage.mangobotcore.api.command.v1.argument;

public final class RequiredArg<T> extends Argument<T> {
    public RequiredArg(String name, String description, ArgumentType<T> type) {
        super(name, description, type);
    }
}
