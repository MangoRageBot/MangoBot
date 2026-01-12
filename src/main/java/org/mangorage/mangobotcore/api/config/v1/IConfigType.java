package org.mangorage.mangobotcore.api.config.v1;

import org.mangorage.mangobotcore.internal.config.ConfigTypeImpl;

import java.util.function.Function;

public sealed interface IConfigType<T> permits ConfigTypeImpl {

    static <T> IConfigType<T> create(Function<String, T> functionA, Function<T, String> functionB) {
        return new ConfigTypeImpl<>(functionA, functionB);
    }

    T get(String value);
    String get(T value);
}
