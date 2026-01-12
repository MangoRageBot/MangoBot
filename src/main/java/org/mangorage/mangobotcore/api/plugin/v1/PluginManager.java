package org.mangorage.mangobotcore.api.plugin.v1;

import org.mangorage.mangobotcore.internal.plugin.PluginManagerImpl;

import java.util.List;

public sealed interface PluginManager permits PluginManagerImpl {
    static PluginManager getInstance() {
        return PluginManagerImpl.INSTANCE;
    }

    PluginContainer getPlugin(String id);
    List<PluginContainer> getPlugins();
}
