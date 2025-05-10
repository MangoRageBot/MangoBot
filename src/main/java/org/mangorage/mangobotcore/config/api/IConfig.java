package org.mangorage.mangobotcore.config.api;

import org.mangorage.mangobotcore.config.internal.ConfigImpl;

import java.nio.file.Path;

public interface IConfig {
    static IConfig create(Path filePath) {
        return create(filePath, false);
    }

    static IConfig create(Path filePath, boolean autoReload) {
        return new ConfigImpl(filePath, autoReload);
    }

    void reload();

    String get(String id);
    void set(String id, String value);

    Path getFilePath();
    void save();
}
