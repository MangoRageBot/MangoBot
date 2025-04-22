package org.mangorage.mangobotcore.plugin.internal;

import org.mangorage.mangobotcore.plugin.api.Metadata;
import org.mangorage.mangobotcore.plugin.internal.dependency.DependencyImpl;

import java.util.List;

public final class MetadataImpl implements Metadata {
    private final String id;

    public MetadataImpl(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<DependencyImpl> dependencies() {
        return List.of();
    }
}
