package org.mangorage.mangobotcore.api.plugin.v1;

import org.mangorage.mangobotcore.internal.plugin.MetadataImpl;

import java.util.List;

public sealed interface Metadata permits MetadataImpl {
    String getId();
    String getName();
    String getType();
    String getVersion();
    List<? extends Dependency> getDependencies();
    IExtraMap getExtraMap();
}