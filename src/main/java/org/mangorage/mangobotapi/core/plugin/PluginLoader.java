/*
 * Copyright (c) 2023-2025. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobotapi.core.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mangorage.basicutils.LogHelper;
import org.mangorage.mangobotapi.core.plugin.impl.Plugin;
import org.mangorage.mangobotapi.core.plugin.misc.Library;
import org.mangorage.mangobotapi.core.plugin.misc.LibraryManager;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class PluginLoader {
    private static final Gson GSON = new GsonBuilder().create();

    private static final Reflections reflections = new Reflections(
            ConfigurationBuilder.build()
                    .setUrls(ClasspathHelper.forClassLoader())
    );

    private static InputStream getFileFromClassLoader(String filePath) {
        InputStream inputStream = PluginManager.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            System.out.println("File not found: " + filePath);
        }
        return inputStream;
    }

    public static void load() {
        LogHelper.info("Gathering Plugin Info...");
        LibraryManager<PluginContainer> manager = new LibraryManager<>();

        reflections.getTypesAnnotatedWith(Plugin.class).forEach(cls -> {
            var pluginAnnotation = cls.getAnnotation(Plugin.class);

            LogHelper.info("Found Plugin with ID '%s', now attempting to find metadata".formatted(pluginAnnotation.id()));

            var metadataIS = getFileFromClassLoader(pluginAnnotation.id() + ".plugin.json");
            if (metadataIS == null) {
                throw new IllegalStateException("Unable to find plugin.json for '%s'".formatted(pluginAnnotation.id()));
            } else {
                var metadata = GSON.fromJson(new InputStreamReader(metadataIS), PluginMetadata.class);

                LogHelper.info("Found Metadata for plugin '%s'".formatted(pluginAnnotation.id()));

                manager.addLibrary(
                        pluginAnnotation.id(),
                        new PluginContainer(
                                pluginAnnotation.id(),
                                cls,
                                metadata
                        )
                );
            }
        });

        LogHelper.info("Organizing Plugin Load Order...");

        for (Library<PluginContainer> library : List.copyOf(manager.getLibraries())) {
            var dependencies = library.getObject().getMetadata().dependencies();
            if (dependencies != null && !dependencies.isEmpty()) {
                LogHelper.info("Found %s dependencies for '%s'".formatted(dependencies.size(), library.getObject().getId()));
                manager.addDependenciesForLibrary(library.getObject().getId(), dependencies);
            } else {
                LogHelper.info("Found no dependencies for '%s'".formatted(library.getObject().getId()));
            }
        }

        LogHelper.info("Loading Plugins...");

        for (Library<PluginContainer> library : manager.getLibrariesInOrder()) {
            loadPlugin(library.getObject());
        }

        LogHelper.info("Finished loading plugins...");
    }

    public static void loadPlugin(PluginContainer container) {
        var pluginId = container.getId();

        if (PluginManager.isLoaded(pluginId)) {
            LogHelper.error("Failed to load plugin '%s', already loaded".formatted(pluginId));
            return;
        }

        LogHelper.info("Loading %s plugin: %s".formatted(container.getType(), pluginId));

        // Register it, so the plugin has access to any info it wishes to have, other then its own reference (At ctor it cant have access to itself, because it doesnt exist yet...)
        PluginManager.registerPluginContainer(container);

        try {
            container.initInstance();
        } catch (Exception e) {
            LogHelper.error("Failed to load plugin: " + pluginId);
            LogHelper.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
