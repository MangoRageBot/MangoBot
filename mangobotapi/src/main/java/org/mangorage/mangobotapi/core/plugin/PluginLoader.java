/*
 * Copyright (c) 2023. MangoRage
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

import org.mangorage.basicutils.LogHelper;
import org.mangorage.mangobotapi.core.plugin.api.AbstractPlugin;
import org.mangorage.mangobotapi.core.plugin.api.AddonPlugin;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;
import org.mangorage.mangobotapi.core.plugin.impl.Plugin;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashSet;
import java.util.Set;

public class PluginLoader {
    private static final Reflections reflections = new Reflections(
            ConfigurationBuilder.build()
                    .forPackages("org", "addon", "com", "net")
    );

    public static void load() {
        LogHelper.info("Loading addons...");
        Set<Class<?>> PLUGINS_CORE = new HashSet<>();
        Set<Class<?>> PLUGINS_ADDON = new HashSet<>();

        reflections.getTypesAnnotatedWith(Plugin.class).forEach(cls -> {
            var pluginAnnotaion = cls.getAnnotation(Plugin.class);
            switch (pluginAnnotaion.type()) {
                case CORE -> {
                    if (!CorePlugin.class.isAssignableFrom(cls)) {
                        LogHelper.error("Failed to load plugin: " + pluginAnnotaion.id() + " (must extend CorePlugin)");
                        return;
                    }
                    PLUGINS_CORE.add(cls);
                }
                case ADDON -> {
                    if (!AddonPlugin.class.isAssignableFrom(cls)) {
                        LogHelper.error("Failed to load plugin: " + pluginAnnotaion.id() + " (must extend AddonPlugin)");
                        return;
                    }
                    PLUGINS_ADDON.add(cls);
                }
            }
        });

        PLUGINS_CORE.forEach(PluginLoader::loadCore);
        PLUGINS_ADDON.forEach(PluginLoader::loadAddon);

        LogHelper.info("Finished loading plugins...");
    }

    public static void loadCore(Class<?> cls) {
        loadPlugin(Plugin.Type.CORE, cls);
    }

    public static void loadAddon(Class<?> cls) {
        loadPlugin(Plugin.Type.ADDON, cls);
    }

    public static void loadPlugin(Plugin.Type type, Class<?> cls) {
        var pluginAnnotaion = cls.getAnnotation(Plugin.class);
        var pluginId = pluginAnnotaion.id();

        if (PluginManager.isLoaded(pluginId)) {
            LogHelper.error("Failed to load plugin: " + pluginId + " (already loaded)");
            return;
        }

        LogHelper.info("Loading plugin: " + pluginId);

        try {
            var plugin = (AbstractPlugin) cls.getDeclaredConstructor().newInstance();
            PluginManager.registerPlugin(type, plugin, pluginId);
        } catch (Exception e) {
            LogHelper.error("Failed to load plugin: " + pluginId);
            LogHelper.error(e.getMessage());
        }
    }
}
