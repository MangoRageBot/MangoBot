package org.mangorage.mangobotcore.internal.config;

import org.mangorage.mangobotcore.api.config.v1.IConfigType;

import java.util.function.Function;

public final class ConfigTypeImpl<T> implements IConfigType<T> {
    private final Function<String, T> functionA;
    private final Function<T, String> functionB;

    public ConfigTypeImpl(Function<String, T> functionA, Function<T, String> functionB) {
        this.functionA = functionA;
        this.functionB = functionB;
    }

    @Override
    public T get(String value) {
        return functionA.apply(value);
    }

    @Override
    public String get(T value) {
        return functionB.apply(value);
    }
}
