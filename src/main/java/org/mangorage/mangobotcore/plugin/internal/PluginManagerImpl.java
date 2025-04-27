package org.mangorage.mangobotcore.plugin.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mangorage.commonutils.log.LogHelper;
import org.mangorage.mangobotcore.plugin.api.MangoBotPlugin;
import org.mangorage.mangobotcore.plugin.api.Plugin;
import org.mangorage.mangobotcore.plugin.api.PluginContainer;
import org.mangorage.mangobotcore.plugin.api.PluginManager;
import org.mangorage.mangobotcore.plugin.internal.dependency.DependencyImpl;
import org.mangorage.mangobotcore.plugin.internal.dependency.Library;
import org.mangorage.mangobotcore.plugin.internal.dependency.LibraryManager;
import org.mangorage.scanner.api.Scanner;
import org.mangorage.scanner.api.ScannerBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PluginManagerImpl implements PluginManager {
    private static final Gson GSON = new GsonBuilder().create();
    public static final PluginManagerImpl INSTANCE = new PluginManagerImpl();

    private static InputStream getFileFromClassLoader(String filePath) {
        InputStream inputStream = PluginManager.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            System.out.println("File not found: " + filePath);
        }
        return inputStream;
    }

    private final Map<String, PluginContainer> plugins = new HashMap<>();

    PluginManagerImpl() {}

    public void load() {
        LogHelper.info("Gathering Plugin Info...");
        LibraryManager<PluginContainerImpl> manager = new LibraryManager<>();

        Scanner scanner = ScannerBuilder.of()
                .addClassloader((URLClassLoader) Thread.currentThread().getContextClassLoader())
                .build();

        scanner.commitScan();

        scanner.findClassesWithAnnotation(MangoBotPlugin.class)
                .forEach(clz -> {
                    var annotation = clz.getAnnotation(MangoBotPlugin.class);
                    if (Plugin.class.isAssignableFrom(clz)) {
                        LogHelper.info("Found Plugin with ID '%s', now attempting to find metadata".formatted(annotation.id()));
                        var metadataIS = getFileFromClassLoader(annotation.id() + ".plugin.json");
                        if (metadataIS == null) {
                            throw new IllegalStateException("Unable to find plugin.json for '%s'".formatted(annotation.id()));
                        } else {
                            var metadata = GSON.fromJson(new InputStreamReader(metadataIS), MetadataImpl.class);

                            LogHelper.info("Found Metadata for plugin '%s'".formatted(annotation.id()));

                            manager.addLibrary(
                                    annotation.id(),
                                    new PluginContainerImpl(
                                            clz,
                                            metadata
                                    )
                            );
                        }
                    }
                });

        LogHelper.info("Organizing Plugin Load Order...");

        for (Library<PluginContainerImpl> library : List.copyOf(manager.getLibraries())) {
            var dependencies = library.getObject().getMetadata().getDependencies();
            if (dependencies != null && !dependencies.isEmpty()) {
                LogHelper.info("Found %s dependencies for '%s'".formatted(dependencies.size(), library.getObject().getMetadata().getId()));
                manager.addDependenciesForLibrary(library.getObject().getMetadata().getId(), dependencies);
            } else {
                LogHelper.info("Found no dependencies for '%s'".formatted(library.getObject().getMetadata().getId()));
            }
        }

        LogHelper.info("Loading Plugins...");

        for (Library<PluginContainerImpl> library : manager.getLibrariesInOrder()) {
            loadPlugin(library.getObject());
        }

        LogHelper.info("Calling init Method on all Plugins...");

        plugins.forEach((k, v) -> {
            if (v.getInstance() instanceof Plugin plugin) {
                plugin.load();
            }
        });

        LogHelper.info("Finished loading plugins...");
    }

    @Override
    public PluginContainer getPlugin(String id) {
        return plugins.get(id);
    }

    @Override
    public List<PluginContainer> getPlugins() {
        return List.copyOf(
                plugins.values()
        );
    }

    public void loadPlugin(PluginContainerImpl container) {
        var pluginId = container.getMetadata().getId();

//        if (PluginManager.isLoaded(pluginId)) {
//            LogHelper.error("Failed to load plugin '%s', already loaded".formatted(pluginId));
//            return;
//        }

        LogHelper.info("Loading plugin: %s".formatted(pluginId));

        // Register it, so the plugin has access to any info it wishes to have, other then its own reference (At ctor it cant have access to itself, because it doesnt exist yet...)
        this.plugins.put(container.getMetadata().getId(), container);

        try {
            container.init();
        } catch (Throwable e) {
            LogHelper.error("Failed to load plugin: " + pluginId);
            LogHelper.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
