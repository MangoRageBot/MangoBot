package org.mangorage.mangobotcore.plugin.internal;

import org.mangorage.mangobotcore.plugin.api.MangoBotPlugin;
import org.mangorage.mangobotcore.plugin.api.Plugin;
import org.mangorage.mangobotcore.plugin.api.PluginManager;
import org.mangorage.scanner.api.Scanner;
import org.mangorage.scanner.api.ScannerBuilder;

import java.util.ArrayList;
import java.util.List;

public final class PluginManagerImpl implements PluginManager {
    public static final PluginManagerImpl INSTANCE = new PluginManagerImpl();

    PluginManagerImpl() {}

    public void load() {
        Scanner scanner = ScannerBuilder.of()
                .addClasspath(Thread.currentThread().getContextClassLoader())
                .build();

        scanner.commitScan();

        List<Plugin> plugins = new ArrayList<>();

        scanner.findClassesWithAnnotation(MangoBotPlugin.class)
                .forEach(clz -> {
                    try {
                        var plugin = clz.getConstructor().newInstance();
                        if (plugin instanceof Plugin mainPlugin) {
                            plugins.add(mainPlugin);
                        }
                    } catch (ReflectiveOperationException e) {}
                });

        plugins.forEach(Plugin::load);
    }
}
