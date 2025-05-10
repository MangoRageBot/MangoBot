package org.mangorage.mangobotcore.config.internal;

import org.mangorage.commonutils.misc.FileMonitor;
import org.mangorage.mangobotcore.config.api.IConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class ConfigImpl implements IConfig {
    private static final Pattern CONFIG_REGEX = Pattern.compile("^\\s*([\\w.\\-]+)\\s*(=)\\s*(['][^']*[']|[\"][^\"]*[\"]|[^#]*)?\\s*(#.*)?$");

    private final Path filePath;
    private final Map<String, String> entries = new ConcurrentHashMap<>();

    public ConfigImpl(Path filePath) {
        this(filePath, false);
    }

    public ConfigImpl(Path filePath, boolean autoReload) {
        this.filePath = filePath;

        try {
            load();

            if (autoReload)
                FileMonitor.getMonitor().register(filePath, kind -> {
                    if (kind == ENTRY_MODIFY)
                        reload(); // Reload it was modified...
                }, ENTRY_MODIFY);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Config...");
        }
    }

    private void load() throws IOException {
        if (Files.exists(filePath)) {
            Files.readAllLines(filePath).forEach(e -> {
                if (CONFIG_REGEX.matcher(e).matches()) {
                    var a = e.split("=", 2);
                    entries.put(a[0], a[1]);
                }
            });
        } else {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
    }

    @Override
    public void reload() {
        entries.clear();
        try {
            load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to reload config...");
        }
    }

    @Override
    public String get(String ID) {
        return entries.get(ID);
    }

    @Override
    public void set(String ID, String value) {
        entries.put(ID, value);
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public void save() {
        try {
            var writer = Files.newBufferedWriter(filePath);
            entries.forEach((key, value) -> {
                var line = "%s=%s".formatted(key, value);
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
