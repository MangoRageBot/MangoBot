package org.mangorage.mangobotcore.api.plugin.v1;

public sealed interface Dependency permits org.mangorage.mangobotcore.internal.plugin.dependency.DependencyImpl {
    String getId();
    DependencyType getType();
}
