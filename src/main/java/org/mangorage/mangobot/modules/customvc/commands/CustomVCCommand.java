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

package org.mangorage.mangobot.modules.customvc.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.BotPermissions;
import org.mangorage.mangobot.modules.customvc.CustomVC;
import org.mangorage.mangobot.modules.customvc.VCConfiguration;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.registry.commands.AutoRegister;
import org.mangorage.mangobotapi.core.util.APIUtil;

import java.util.List;

@AutoRegister.BasicCommand
public class CustomVCCommand implements IBasicCommand {
    private static final CommandResult NOT_OWNER = CommandResult.of("Your not owner of this VC!");
    private static final CommandResult USER_IS_BOT = CommandResult.of("Cant remove/add/reset this user. Its the Bot.");

    private static final List<Permission> PERMISSIONS = List.of(
            Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK
    );

    private static final List<Permission> PRIVATE_PERMISSIONS = List.of(
            Permission.VIEW_CHANNEL,
            Permission.VOICE_CONNECT
    );


    @NotNull
    @Override
    public CommandResult execute(Message message, Arguments args) {
        if (!message.isFromGuild()) return CommandResult.GUILD_ONLY;
        var guild = message.getGuild();
        var member = message.getMember();

        if (member == null) return CommandResult.FAIL;

        String subcmd = args.get(0);
        String arg = args.get(1);

        if (subcmd.equalsIgnoreCase("configure")) {
            if (!BotPermissions.CUSTOM_VC_ADMIN.hasPermission(member)) return CommandResult.NO_PERMISSION;
            if (arg == null) return CommandResult.FAIL;

            CustomVC.configure(guild, arg);
            Bot.DEFAULT_SETTINGS.apply(message.reply("Configured CustomVC for this guild!")).queue();

            return CommandResult.PASS;
        } else if (subcmd.equalsIgnoreCase("setBitrate")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var bitrate = Integer.parseInt(arg);

                if (bitrate < 8000 || bitrate > 128000) {
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Bitrate must be between 8000 and 128000")).queue();
                    return CommandResult.PASS;
                }


                APIUtil.getLazyAudioChannelManager(member).ifPresent(audioChannelManager -> {
                    VCConfiguration VC = CustomVC.getConfiguration(member.getUser());
                    VC.setBitrate(bitrate);
                    audioChannelManager.setBitrate(bitrate).queue();
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Set your VC's bitrate to %s".formatted(arg))).queue();
                });
            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        } else if (subcmd.equalsIgnoreCase("setUserlimit")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var userlimit = Integer.parseInt(arg);
                if (userlimit < 0 || userlimit > 99) {
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Userlimit must be between 0 (no limit) and 99")).queue();
                    return CommandResult.PASS;
                }

                APIUtil.getLazyAudioChannelManager(member).ifPresent(audioChannelManager -> {
                    audioChannelManager.setUserLimit(userlimit).queue();
                    VCConfiguration VC = CustomVC.getConfiguration(member.getUser());
                    VC.setUserLimit(userlimit);
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Set your VC's userlimit to %s".formatted(arg))).queue();
                });
            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        } else if (subcmd.equalsIgnoreCase("setPrivate")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var privateVC = Boolean.parseBoolean(arg);

                APIUtil.getLazyAudioChannelManager(member).ifPresent(manager -> {
                    if (privateVC) {
                        manager.putRolePermissionOverride(guild.getPublicRole().getIdLong(), List.of(), PRIVATE_PERMISSIONS).queue();
                    } else {
                        manager.putRolePermissionOverride(guild.getPublicRole().getIdLong(), List.of(), List.of()).queue();
                    }
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Set your vc private status to %s".formatted(arg))).queue();
                });

            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        } else if (subcmd.equalsIgnoreCase("allowUser")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var user = Long.parseLong(arg);
                if (user == Bot.getJDAInstance().getSelfUser().getIdLong()) return USER_IS_BOT;

                APIUtil.getLazyAudioChannelManager(member).ifPresent(manager -> {
                    manager.putMemberPermissionOverride(user, List.of(), List.of()).queue();
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Added user %s to your VC".formatted(arg))).queue();
                });

            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        } else if (subcmd.equalsIgnoreCase("denyUser")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var user = Long.parseLong(arg);
                if (user == Bot.getJDAInstance().getSelfUser().getIdLong()) return USER_IS_BOT;

                APIUtil.getLazyAudioChannelManager(member).ifPresent(manager -> {
                    manager.putMemberPermissionOverride(user, List.of(), PERMISSIONS).queue();
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Removed user %s from your VC".formatted(arg))).queue();
                });

            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        } else if (subcmd.equalsIgnoreCase("kickUser")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var user = Long.parseLong(arg);

                APIUtil.getLazyVoiceChannel(member).ifPresent(voiceChannel -> {
                    var memberToKick = voiceChannel.getGuild().getMemberById(user);
                    if (memberToKick == null) return;
                    var result = voiceChannel.getMembers().stream().filter(m -> m.getIdLong() == user).findFirst();
                    if (result.isPresent()) {
                        var m = result.get();
                        guild.kickVoiceMember(m).queue();
                        Bot.DEFAULT_SETTINGS.apply(message.reply("Kicked user %s from your VC".formatted(m.getEffectiveName()))).queue();
                    } else {
                        Bot.DEFAULT_SETTINGS.apply(message.reply("Could not find user in your VC")).queue();
                    }
                });

            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        } else if (subcmd.equalsIgnoreCase("setName")) {
            if (!APIUtil.inVC(member)) return CommandResult.NEED_TO_BE_IN_VC;
            if (!CustomVC.isOwner(guild, member)) return NOT_OWNER;

            try {
                var name = args.getFrom(1);

                if (name.isBlank()) {
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Name cannot be blank")).queue();
                    return CommandResult.PASS;
                }

                if (name.length() > 100) {
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Name cannot be longer than 100 characters")).queue();
                    return CommandResult.PASS;
                }


                APIUtil.getLazyAudioChannelManager(member).ifPresent(manager -> {
                    manager.setName(name).queue();
                    VCConfiguration VC = CustomVC.getConfiguration(member.getUser());
                    VC.setName(name);
                    Bot.DEFAULT_SETTINGS.apply(message.reply("Set your VC's name to %s".formatted(name))).queue();
                });

            } catch (NumberFormatException e) {
                return CommandResult.FAIL;
            }
        }


        return CommandResult.PASS;
    }


    @Override
    public String commandId() {
        return "customvc";
    }
}
