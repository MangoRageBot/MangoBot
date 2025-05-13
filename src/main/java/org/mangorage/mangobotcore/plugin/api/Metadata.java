package org.mangorage.mangobotcore.plugin.api;

import org.mangorage.mangobotcore.plugin.internal.MetadataImpl;

import java.util.List;

public sealed interface Metadata permits MetadataImpl {
    String getId();
    String getName();
    String getType();
    String getVersion();
    List<? extends Dependency> getDependencies();
    IExtraMap getExtraMap();
}