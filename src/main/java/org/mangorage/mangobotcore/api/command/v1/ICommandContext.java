package org.mangorage.mangobotcore.api.command.v1;

public interface ICommandContext {
    <T> T get(Class<T> tClass);
    boolean hasType(Class<?> tClass);
}
