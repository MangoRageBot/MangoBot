package org.mangorage.mangobotcore.api.jda.permission.v1;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.mangorage.mangobotcore.api.command.v1.CommandContext;
import org.mangorage.mangobotcore.api.command.v1.PermissionNode;
import org.mangorage.mangobotcore.api.util.data.IUniqueIdHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "jda_permission_nodes")
public final class JDAPermissionNode implements PermissionNode<Message>, IUniqueIdHolder<String> {

    @Embeddable
    record GuildUser(Long guildId, Long userId) {}

    @Embeddable
    record GuildRole(Long guildId, Long roleId) {}

    public static JDAPermissionNode create(String id) {
        return new JDAPermissionNode(id);
    }

    @Id
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guild_roles", joinColumns = @JoinColumn(name = "permission_node_id"))
    private final Set<GuildRole> roleIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guild_users", joinColumns = @JoinColumn(name = "permission_node_id"))
    private final Set<GuildUser> userIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "required_permissions", joinColumns = @JoinColumn(name = "permission_node_id"))
    private final Set<Permission> requiredDiscordPermissions = new HashSet<>();

    public JDAPermissionNode() {}

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
