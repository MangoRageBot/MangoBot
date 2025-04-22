package org.mangorage.mangobotcore.plugin.api;

import org.mangorage.mangobotcore.plugin.internal.PluginManagerImpl;

public sealed interface PluginManager permits PluginManagerImpl {
    static PluginManager getInstance() {
        return PluginManagerImpl.INSTANCE;
    }

    PluginContainer getPlugin(String id);
}
