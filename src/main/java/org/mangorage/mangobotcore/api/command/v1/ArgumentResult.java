package org.mangorage.mangobotcore.api.command.v1;

public record ArgumentResult<T>(T value, boolean argumentConsumed) {
    public static <T> ArgumentResult<T> empty() {
        return new ArgumentResult<>(null, false);
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isArgumentConsumed() {
        return argumentConsumed;
    }
}
