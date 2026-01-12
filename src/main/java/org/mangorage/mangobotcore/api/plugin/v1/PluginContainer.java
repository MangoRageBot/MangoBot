package org.mangorage.mangobotcore.api.plugin.v1;

import org.mangorage.mangobotcore.internal.plugin.PluginContainerImpl;

public sealed interface PluginContainer permits PluginContainerImpl {
    <T> T getInstance(Class<T> tClass);
    Object getInstance();
    Metadata getMetadata();
}
