package org.mangorage.mangobotcore.config.internal;

import org.mangorage.mangobotcore.config.api.IConfig;
import org.mangorage.mangobotcore.config.api.IConfigSetting;
import org.mangorage.mangobotcore.config.api.IConfigType;

public final class ConfigSettingImpl<T> implements IConfigSetting<T> {
    private final IConfig config;
    private final String id;
    private final IConfigType<T> configType;
    private final T defaultValue;

    public ConfigSettingImpl(IConfig config, String id, IConfigType<T> configType, T defaultValue) {
        this.config = config;
        this.id = id;
        this.configType = configType;
        this.defaultValue = defaultValue;
    }

    @Override
    public T get() {
        var result = configType.get(config.get(id));
        return result != null ? result : defaultValue;
    }

    @Override
    public void set(T value) {
        config.set(id, configType.get(value));
        config.save();
    }
}
