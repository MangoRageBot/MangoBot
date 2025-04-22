package org.mangorage.mangobotcore.plugin.api;

import org.mangorage.mangobotcore.plugin.internal.PluginContainerImpl;

public sealed interface PluginContainer permits PluginContainerImpl {
    <T> T getInstance(Class<T> tClass);
    Object getInstance();
    Metadata getMetadata();
}
