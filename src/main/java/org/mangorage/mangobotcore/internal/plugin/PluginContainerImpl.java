package org.mangorage.mangobotcore.internal.plugin;

import org.mangorage.mangobotcore.api.plugin.v1.PluginContainer;

public final class PluginContainerImpl implements PluginContainer {
    private final Class<?> pluginClazz;
    private final MetadataImpl metadata;

    private volatile Object pluginInstance;

    public PluginContainerImpl(Class<?> pluginClazz, MetadataImpl metadata) {
        this.pluginClazz = pluginClazz;
        this.metadata = metadata;
    }

    void init() throws ReflectiveOperationException {
        pluginInstance = pluginClazz.getConstructor().newInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> tClass) {
        return (T) pluginInstance;
    }

    public Object getInstance() {
        return pluginInstance;
    }

    @Override
    public MetadataImpl getMetadata() {
        return metadata;
    }
}
