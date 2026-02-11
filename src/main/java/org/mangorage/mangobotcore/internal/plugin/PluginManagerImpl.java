package org.mangorage.mangobotcore.internal.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mangorage.bootstrap.api.logging.IDeferredMangoLogger;
import org.mangorage.bootstrap.api.logging.ILoggerFactory;
import org.mangorage.mangobotcore.api.plugin.v1.IPluginInfoGetter;
import org.mangorage.mangobotcore.api.plugin.v1.MangoBotPlugin;
import org.mangorage.mangobotcore.api.plugin.v1.Metadata;
import org.mangorage.mangobotcore.api.plugin.v1.Plugin;
import org.mangorage.mangobotcore.api.plugin.v1.PluginContainer;
import org.mangorage.mangobotcore.api.plugin.v1.PluginManager;
import org.mangorage.mangobotcore.internal.plugin.dependency.Library;
import org.mangorage.mangobotcore.internal.plugin.dependency.LibraryManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public final class PluginManagerImpl implements PluginManager {
    private static final IDeferredMangoLogger LOGGER = ILoggerFactory.getDefault().getWrappedProvider("slf4j", PluginManagerImpl.class);

    private static final Gson GSON = new GsonBuilder().create();
    public static final PluginManagerImpl INSTANCE = new PluginManagerImpl();

    private final Map<String, PluginContainer> plugins = new HashMap<>();

    PluginManagerImpl() {}

    public void load() {
        final var logger = LOGGER.get();

        logger.info("Gathering Plugin Info...");
        LibraryManager<PluginContainerImpl> manager = new LibraryManager<>();



        ServiceLoader.load(PluginManagerImpl.class.getModule().getLayer(), Plugin.class)
                .stream()
                .toList()
                .forEach(plugin -> {
                    var clz = plugin.type();
                    var annotation = clz.getAnnotation(MangoBotPlugin.class);
                    if (Plugin.class.isAssignableFrom(clz)) {
                        logger.info("Found Plugin with ID '%s', now attempting to find metadata".formatted(annotation.id()));

                        InputStream metadataIS = null;

                        try {
                            metadataIS = clz.getModule().getResourceAsStream(annotation.id() + ".plugin.json");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (metadataIS == null) {
                            throw new IllegalStateException("Unable to find plugin.json for '%s'".formatted(annotation.id()));
                        } else {
                            var metadata = GSON.fromJson(new InputStreamReader(metadataIS), MetadataImpl.class);

                            logger.info("Found Metadata for plugin '%s'".formatted(annotation.id()));

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

        logger.info("Organizing Plugin Load Order...");

        for (Library<PluginContainerImpl> library : List.copyOf(manager.getLibraries())) {
            var dependencies = library.getObject().getMetadata().getDependencies();
            if (dependencies != null && !dependencies.isEmpty()) {
                logger.info("Found %s dependencies for '%s'".formatted(dependencies.size(), library.getObject().getMetadata().getId()));
                manager.addDependenciesForLibrary(library.getObject().getMetadata().getId(), dependencies);
            } else {
                logger.info("Found no dependencies for '%s'".formatted(library.getObject().getMetadata().getId()));
            }
        }

        logger.info("Giving Metadata info out...");

        ServiceLoader.load(PluginManagerImpl.class.getModule().getLayer(), IPluginInfoGetter.class)
                .stream()
                .forEach(provider -> {
                    final var list = manager.getLibraries()
                            .stream()
                            .map(library -> (Metadata) library.getObject().getMetadata())
                            .toList();
                    provider.get().onGet(list);
                });


        logger.info("Loading Plugins...");

        for (Library<PluginContainerImpl> library : manager.getLibrariesInOrder()) {
            loadPlugin(library.getObject());
        }

        logger.info("Calling init Method on all Plugins...");

        plugins.forEach((k, v) -> {
            if (v.getInstance() instanceof Plugin plugin) {
                plugin.load();
            }
        });

        logger.info("Finished loading plugins...");
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

        LOGGER.get().info("Loading plugin: %s".formatted(pluginId));

        // Register it, so the plugin has access to any info it wishes to have, other then its own reference (At ctor it cant have access to itself, because it doesnt exist yet...)
        this.plugins.put(container.getMetadata().getId(), container);

        try {
            container.init();
        } catch (Throwable e) {
            LOGGER.get().error("Failed to load plugin: " + pluginId);
            LOGGER.get().error(e.getMessage());
            e.printStackTrace();
        }
    }
}
