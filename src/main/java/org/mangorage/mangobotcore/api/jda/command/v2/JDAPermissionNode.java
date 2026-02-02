package org.mangorage.mangobotcore.api.jda.command.v2;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.mangorage.mangobotcore.api.command.v1.CommandContext;
import org.mangorage.mangobotcore.api.command.v1.PermissionNode;

import java.util.ArrayList;
import java.util.List;

public final class JDAPermissionNode implements PermissionNode<Message> {

    record GuildUser(Long guildId, Long userId) {}
    record GuildRole(Long guildId, Long roleId) {}

    public static JDAPermissionNode create(String id) {
        return new JDAPermissionNode(id);
    }

    private final String id;
    private final List<GuildRole> roleIds = new ArrayList<>();
    private final List<GuildUser> userIds = new ArrayList<>();
    private final List<Permission> requiredDiscordPermissions = new ArrayList<>();

    private JDAPermissionNode(String id) {
        this.id = id;
    }

    public void authorizeGuildUser(Member member) {
        authorizeUser(member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public void revokeGuildUser(Member member) {
        revokeUser(member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public void authorizeUser(Long guildId, long userId) {
        userIds.add(new GuildUser(guildId, userId));
    }

    public void revokeUser(Long guildId, long userId) {
        userIds.remove(new GuildUser(guildId, userId));
    }

    public void addRequiredPermission(Permission permission) {
        requiredDiscordPermissions.add(permission);
    }

    public void removeRequiredPermission(Permission permission) {
        requiredDiscordPermissions.remove(permission);
    }

    public void authoriseRoleId(Long guildId, Long roleId) {
        roleIds.add(new GuildRole(guildId, roleId));
    }

    public void revokeRoleId(Long guildId, Long roleId) {
        roleIds.remove(new GuildRole(guildId, roleId));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean hasPermission(CommandContext<Message> commandContext) {
        final var isGuild = commandContext.getContextObject().isFromGuild();
        if (isGuild) {
            final var member = commandContext.getContextObject().getMember();
            if (member == null) {
                return false;
            }
            final var guildId = member.getGuild().getIdLong();

            if (roleIds.isEmpty() && userIds.isEmpty() && requiredDiscordPermissions.isEmpty()) {
                return member.hasPermission(Permission.ADMINISTRATOR);
            } else {
                for (Role role : member.getRoles()) {
                    if (roleIds.contains(new GuildRole(guildId, role.getIdLong())))
                        return true;
                }
                return userIds.contains(new GuildUser(guildId, member.getIdLong()));
            }
        } else {
            return userIds.contains(new GuildUser(null, commandContext.getContextObject().getAuthor().getIdLong()));
        }
    }
}
