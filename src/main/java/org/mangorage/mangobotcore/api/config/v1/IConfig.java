package org.mangorage.mangobotcore.api.config.v1;

import org.mangorage.mangobotcore.internal.config.ConfigImpl;

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
