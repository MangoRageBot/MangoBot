package org.mangorage.mangobotcore.plugin.api;

import org.mangorage.mangobotcore.plugin.internal.ExtraMap;

public sealed interface IExtraMap permits ExtraMap {
    <T> T getKey(String key, Class<T> tClass);
}
