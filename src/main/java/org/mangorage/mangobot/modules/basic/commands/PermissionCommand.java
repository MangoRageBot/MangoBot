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
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.config.BotPermissions;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.registry.BasicPermission;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;

import java.util.List;

public class PermissionCommand implements IBasicCommand {

    @NotNull
    @Override
    public CommandResult execute(Message message, Arguments args) {
        if (!message.isFromGuild()) return CommandResult.GUILD_ONLY;
        var guild = message.getGuild();
        var member = message.getMember();
        if (member == null) return CommandResult.FAIL;
        if (!BotPermissions.PERMISSION_ADMIN.hasPermission(member)) return CommandResult.NO_PERMISSION;
        var settings = Bot.DEFAULT_SETTINGS;

        /**
         *
         *
         * !perm addRole permid id
         * !perm removeRole permid id
         *
         * !perm addPerm permid id
         * !perm removePerm permid id
         *
         * !perm list
         * !perm info permid
         * !perm discordperms
         */

        String subcmd = args.get(0);
        String permId = args.get(1);
        String id = args.get(2);

        // Do checking!

        if (PermissionRegistry.getPermission(permId) == null) {
            settings.apply(message.reply("Bot Permission '%s' not found!".formatted(permId))).queue();
            return CommandResult.PASS;
        }

        if (subcmd.equals("addPerm") || subcmd.equals("removePerm")) {
            try {
                Permission.valueOf(id);
            } catch (IllegalArgumentException e) {
                settings.apply(message.reply("Discord Permission '%s' not found!".formatted(id))).queue();
                return CommandResult.PASS;
            }
        }

        // Final Logic!


        if (subcmd.equals("addRole")) {
            PermissionRegistry.getPermission(permId).addRole(guild.getId(), id);
            return CommandResult.PASS;
        } else if (subcmd.equals("removeRole")) {
            PermissionRegistry.getPermission(permId).removeRole(guild.getId(), id);
            return CommandResult.PASS;
        } else if (subcmd.equals("addPerm")) {
            PermissionRegistry.getPermission(permId).addPermission(guild.getId(), Permission.valueOf(id));
            return CommandResult.PASS;
        } else if (subcmd.equals("removePerm")) {
            PermissionRegistry.getPermission(permId).removePermission(guild.getId(), Permission.valueOf(id));
            return CommandResult.PASS;
        } else if (subcmd.equals("info")) {
            BasicPermission permission = PermissionRegistry.getPermission(permId);

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
            PermissionRegistry.getPermissions().forEach((perm) -> {
                list.append(perm).append("\n");
            });
            settings.apply(message.reply(list.toString())).queue();
            return CommandResult.PASS;
        }

        return CommandResult.PASS;
    }


    @Override
    public String commandId() {
        return "permission";
    }
}
