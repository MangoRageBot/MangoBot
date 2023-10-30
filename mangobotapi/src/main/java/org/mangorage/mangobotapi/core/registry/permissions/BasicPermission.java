/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobotapi.core.registry.permissions;

import com.google.gson.annotations.Expose;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class handles Permissions for Commands that are ran on guilds only!
 * <p>
 * To have permissions for commands that are ran for hardcoded values,
 * use the allowedGuilds/Users on {@link org.mangorage.mangobotapi.core.commands.ICommand}
 */

// TODO: Have DataHandler handle data for this.

public class BasicPermission {
    public static BasicPermission create(String id) {
        return new BasicPermission(id);
    }

    private final HashSet<Permission> DISCORD_PERMISSIONS = new HashSet<>();
    private final HashSet<String> ROLES = new HashSet<>();

    @Expose
    protected final HashMap<String, Node> NODES = new HashMap<>();
    @Expose
    private final String id;

    private BasicPermission(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getInfo(Guild guild) {
        StringBuilder builder = new StringBuilder();
        Node node = NODES.get(guild.getId());
        if (node == null) return "No Permissions for this guild";
        builder.append("Permission Info for permissionID: ").append(getId()).append("\n\n");

        if (!node.DISCORD_PERMISSIONS.isEmpty()) {
            builder.append("Discord Permissions for ").append(guild.getName()).append("\n");
            node.DISCORD_PERMISSIONS.forEach(
                    (perm) -> builder.append("Permission: ").append(perm.getName()).append(" -> ").append(perm).append("\n")
            );
            builder.append("\n");
        }

        if (!node.ROLES.isEmpty()) {
            builder.append("Roles for ").append(guild.getName()).append("\n");
            node.ROLES.forEach(
                    (role) -> builder.append("Role: ").append("<@&%s> -> %s".formatted(role, role)).append("\n")
            );
        }
        return builder.toString();
    }

    public void reset(String guildId) {
        var node = NODES.computeIfAbsent(guildId, this::createNode);
        node.ROLES.clear();
        node.DISCORD_PERMISSIONS.clear();

        // Set default's back...
        node.ROLES.addAll(ROLES);
        node.DISCORD_PERMISSIONS.addAll(DISCORD_PERMISSIONS);

        // Save
        save();
    }

    private Node createNode(String guildID) {
        return new Node(this, guildID, DISCORD_PERMISSIONS, ROLES);
    }

    private void save() {
    }

    public boolean hasPermission(Member member) {
        return NODES.computeIfAbsent(member.getGuild().getId(), this::createNode).hasPermission(member);
    }

    public void addRole(String guildId, String role) {
        NODES.computeIfAbsent(guildId, this::createNode).ROLES.add(role);
        save();
    }


    public void addPermission(String guildId, Permission permission) {
        NODES.computeIfAbsent(guildId, this::createNode).DISCORD_PERMISSIONS.add(permission);
        save();
    }

    public void removeRole(String guildId, String role) {
        NODES.computeIfAbsent(guildId, this::createNode).ROLES.remove(role);
        save();
    }

    public void removePermission(String guildId, Permission permission) {
        NODES.computeIfAbsent(guildId, this::createNode).DISCORD_PERMISSIONS.remove(permission);
        save();
    }

    // DEFAULT
    public void addRole(String role) {
        ROLES.add(role);
    }

    public void addPermission(Permission permission) {
        DISCORD_PERMISSIONS.add(permission);
    }


    private static class Node {
        @Expose
        private final HashSet<Permission> DISCORD_PERMISSIONS = new HashSet<>();
        @Expose
        private final HashSet<String> ROLES = new HashSet<>();
        @Expose
        private final String guildID;

        private Node(BasicPermission permission, String guildID, HashSet<Permission> permissions, HashSet<String> roles) {
            this.guildID = guildID;
            DISCORD_PERMISSIONS.addAll(permissions);
            ROLES.addAll(roles);
            permission.save();
        }


        private boolean hasPermission(Member member) {
            if (!DISCORD_PERMISSIONS.isEmpty() && member.hasPermission(DISCORD_PERMISSIONS))
                return true;

            AtomicBoolean result = new AtomicBoolean(false);

            for (Role role : member.getRoles()) {
                if (ROLES.contains(role.getId())) {
                    result.set(true);
                    break;
                }
            }

            return result.get();
        }
    }
}
