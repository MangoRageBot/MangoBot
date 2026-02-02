package org.mangorage.mangobotcore.internal.permission;

import org.mangorage.mangobotcore.api.command.v1.CommandContext;
import org.mangorage.mangobotcore.api.command.v1.PermissionNode;

public final class EmptyPermissionNode<C> implements PermissionNode<C> {

    private static final EmptyPermissionNode<?> INSTANCE = new EmptyPermissionNode<>();

    public static <C> EmptyPermissionNode<C> of() {
        return (EmptyPermissionNode<C>) INSTANCE;
    }

    EmptyPermissionNode() {}

    @Override
    public String getId() {
        return "empty";
    }

    @Override
    public boolean hasPermission(CommandContext<C> commandContext) {
        return true;
    }
}
