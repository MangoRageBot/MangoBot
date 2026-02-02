package org.mangorage.mangobotcore.api.command.v1;

public interface PermissionNode<C> {
    String getId();
    boolean hasPermission(CommandContext<C> commandContext);
}
