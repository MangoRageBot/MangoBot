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

package org.mangorage.mangobot.modules.basic.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobot.core.BotPermissions;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;
import org.mangorage.mangobotapi.core.registry.permissions.BasicPermission;
import org.mangorage.mangobotapi.core.registry.permissions.PermissionRegistry;

import java.util.List;

public class PermissionCommand implements IBasicCommand {
    private final CorePlugin corePlugin;
    private final PermissionRegistry permissionRegistry;

    public PermissionCommand(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
        this.permissionRegistry = corePlugin.getPermissionRegistry();
    }




    @NotNull
    @Override
    public CommandResult execute(Message message, Arguments args) {
        if (!message.isFromGuild()) return CommandResult.GUILD_ONLY;
        var guild = message.getGuild();
        var member = message.getMember();
        if (member == null) return CommandResult.FAIL;
        if (!BotPermissions.PERMISSION_ADMIN.hasPermission(member)) return CommandResult.NO_PERMISSION;
        var settings = corePlugin.getMessageSettings();

        /**
         * !perm addRole permid id
         * !perm removeRole permid id
         *
         * !perm addPerm permid id
         * !perm removePerm permid id
         *
         * !perm list
         * !perm info permid
         * !perm discordperms
         * !perm reset permid (Resets the Permission stuff for your guild) (WIP)
         */

        String subcmd = args.get(0);
        String permId = args.get(1);
        String id = args.get(2);


        if (subcmd.equals("addPerm") || subcmd.equals("removePerm") || subcmd.equals("reset")) {
            try {
                Permission.valueOf(id);
            } catch (IllegalArgumentException e) {
                settings.apply(message.reply("Discord Permission '%s' not found!".formatted(id))).queue();
                return CommandResult.PASS;
            }
        }

        // Final Logic!


        if (subcmd.equals("addRole")) {
            if (permissionRegistry.getPermission(permId) == null) {
                settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
                return CommandResult.PASS;
            }

            permissionRegistry.getPermission(permId).addRole(guild.getId(), id);
            return CommandResult.PASS;
        } else if (subcmd.equals("removeRole")) {
            if (permissionRegistry.getPermission(permId) == null) {
                settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
                return CommandResult.PASS;
            }

            permissionRegistry.getPermission(permId).removeRole(guild.getId(), id);
            return CommandResult.PASS;
        } else if (subcmd.equals("addPerm")) {
            if (permissionRegistry.getPermission(permId) == null) {
                settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
                return CommandResult.PASS;
            }

            permissionRegistry.getPermission(permId).addPermission(guild.getId(), Permission.valueOf(id));
            return CommandResult.PASS;
        } else if (subcmd.equals("removePerm")) {
            if (permissionRegistry.getPermission(permId) == null) {
                settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
                return CommandResult.PASS;
            }

            permissionRegistry.getPermission(permId).removePermission(guild.getId(), Permission.valueOf(id));
            return CommandResult.PASS;
        } else if (subcmd.equals("info")) {
            if (permissionRegistry.getPermission(permId) == null) {
                settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
                return CommandResult.PASS;
            }

            BasicPermission permission = permissionRegistry.getPermission(permId);

            settings.apply(message.reply(permission.getInfo(guild))).setAllowedMentions(List.of()).setSuppressedNotifications(true).queue();

            return CommandResult.PASS;
        } else if (subcmd.equals("discordperms")) {
            StringBuilder builder = new StringBuilder();
            builder.append("All Discord Permissions:").append("\n\n");

            for (Permission perm : Permission.values()) {
                builder.append(perm.getName()).append(" -> ").append(perm).append("\n");
            }

            settings.apply(message.reply(builder.toString())).queue();
            return CommandResult.PASS;
        } else if (subcmd.equals("list")) {
            StringBuilder list = new StringBuilder();
            list.append("Permissions:").append("\n\n");
            permissionRegistry.getPermissions().forEach((perm) -> {
                list.append(perm).append("\n");
            });
            settings.apply(message.reply(list.toString())).queue();
            return CommandResult.PASS;
        } else if (subcmd.equals("reset")) {
            if (permissionRegistry.getPermission(permId) == null) {
                settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
                return CommandResult.PASS;
            }

            permissionRegistry.getPermission(permId).reset(guild.getId());

            return CommandResult.PASS;
        } else if (subcmd.equalsIgnoreCase("test")) {
            var userID = args.get(2);
            Member member2 = guild.getMemberById(userID);
            if (member2 == null) return CommandResult.PASS;
            var result = permissionRegistry.getPermission(permId).hasPermission(member2);
            message.reply("Has Permission: %s".formatted(result)).queue();
            return CommandResult.PASS;
        }

        return CommandResult.FAIL;
    }


    @Override
    public String commandId() {
        return "permission";
    }
}
