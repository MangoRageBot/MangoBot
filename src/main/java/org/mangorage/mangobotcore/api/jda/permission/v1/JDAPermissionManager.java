package org.mangorage.mangobotcore.api.jda.permission.v1;

import org.mangorage.mangobotcore.api.util.data.DatabaseHandler;

import java.util.HashMap;
import java.util.Map;

public final class JDAPermissionManager {

    public static JDAPermissionManager create(DatabaseHandler<String, JDAPermissionNode> databaseHandler) {
        return new JDAPermissionManager(databaseHandler);
    }

    private final DatabaseHandler<String, JDAPermissionNode> databaseHandler;

    private final Map<String, JDAPermissionNode> permissionNodes = new HashMap<>();

    JDAPermissionManager(DatabaseHandler<String, JDAPermissionNode> databaseHandler) {
        this.databaseHandler = databaseHandler;
        databaseHandler.loadEntitiesFromDatabase().forEach(node -> permissionNodes.put(node.getId(), node));
    }

    public JDAPermissionNode getPermissionNode(String id) {
        return permissionNodes.computeIfAbsent(id, JDAPermissionNode::create);
    }

    public void savePermissionNode(JDAPermissionNode node) {
        databaseHandler.saveEntity(node);
    }

}
