package org.mangorage.mangobotcore.config.api;

import org.mangorage.mangobotcore.config.internal.ConfigTypeImpl;

import java.util.function.Function;

public sealed interface IConfigType<T> permits org.mangorage.mangobotcore.config.internal.ConfigTypeImpl {

    static <T> IConfigType<T> create(Function<String, T> functionA, Function<T, String> functionB) {
        return new ConfigTypeImpl<>(functionA, functionB);
    }

    T get(String value);
    String get(T value);
}
