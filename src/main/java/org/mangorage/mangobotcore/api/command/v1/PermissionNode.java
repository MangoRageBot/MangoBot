package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.internal.permission.EmptyPermissionNode;

public interface PermissionNode<C> {
    static <C> PermissionNode<C> empty() {
        return EmptyPermissionNode.of();
    }

    String getId();
    boolean hasPermission(CommandContext<C> commandContext);
}
