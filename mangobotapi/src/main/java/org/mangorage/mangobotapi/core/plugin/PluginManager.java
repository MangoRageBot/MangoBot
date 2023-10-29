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

import org.mangorage.mangobotapi.core.plugin.api.Plugin;
import org.mangorage.mangobotapi.core.plugin.impl.IPlugin;

import java.util.HashMap;
import java.util.List;

public class PluginManager {
    private static final HashMap<String, PluginContainer> PLUGIN_CONTAINERS = new HashMap<>();

    public static List<PluginContainer> getPluginContainers() {
        return PLUGIN_CONTAINERS.values().stream().toList();
    }

    public static <T extends Plugin> T getPlugin(String id, Class<T> type) {
        return type.cast(PLUGIN_CONTAINERS.get(id).plugin());
    }

    public static IPlugin getPlugin(String id) {
        return PLUGIN_CONTAINERS.get(id).plugin();
    }

    public static boolean isLoaded(String id) {
        return PLUGIN_CONTAINERS.containsKey(id);
    }

    protected static void registerPlugin(PluginType type, IPlugin plugin, String id) {
        PLUGIN_CONTAINERS.put(id, new PluginContainer(type, plugin, id));
    }
}
