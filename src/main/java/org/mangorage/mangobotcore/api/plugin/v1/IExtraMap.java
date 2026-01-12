package org.mangorage.mangobotcore.api.plugin.v1;

import org.mangorage.mangobotcore.internal.plugin.ExtraMap;

public sealed interface IExtraMap permits ExtraMap {
    <T> T getKey(String key, Class<T> tClass);
}
