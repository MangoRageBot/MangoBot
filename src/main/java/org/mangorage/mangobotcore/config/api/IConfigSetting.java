package org.mangorage.mangobotcore.config.api;

import org.mangorage.mangobotcore.config.internal.ConfigSettingImpl;

public sealed interface IConfigSetting<T> permits ConfigSettingImpl {

    static <T> IConfigSetting<T> create(IConfig config, String id, IConfigType<T> configType, T defaultValue) {
        return new ConfigSettingImpl<>(config, id, configType, defaultValue);
    }

    T get();
    void set(T value);
}
