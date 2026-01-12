package org.mangorage.mangobotcore.internal.plugin;

import com.google.gson.annotations.Expose;
import org.mangorage.mangobotcore.api.plugin.v1.Metadata;
import org.mangorage.mangobotcore.internal.plugin.dependency.DependencyImpl;

import java.util.List;

public record MetadataImpl(
        @Expose
        String id,

        @Expose
        String name,

        @Expose
        String type,

        @Expose
        String version,

        @Expose
        List<DependencyImpl> dependencies,

        @Expose
        ExtraMap extraInfo
) implements Metadata {

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public List<DependencyImpl> getDependencies() {
        return dependencies;
    }

    @Override
    public ExtraMap getExtraMap() {
        return extraInfo;
    }
}