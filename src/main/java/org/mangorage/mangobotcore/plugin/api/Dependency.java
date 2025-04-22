package org.mangorage.mangobotcore.plugin.api;

public sealed interface Dependency permits org.mangorage.mangobotcore.plugin.internal.dependency.DependencyImpl {
    String getId();
    DependencyType getType();
}
