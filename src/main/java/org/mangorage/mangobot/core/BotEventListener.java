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

package org.mangorage.mangobot.core;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.mangorage.basicutils.LogHelper;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.events.SlashCommandEvent;
import org.mangorage.mangobotapi.core.events.discord.DButtonInteractionEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageDeleteEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageUpdateEvent;
import org.mangorage.mangobotapi.core.events.discord.DReactionEvent;
import org.mangorage.mangobotapi.core.events.discord.DStringSelectInteractionEvent;
import org.mangorage.mangobotapi.core.events.discord.DVoiceUpdateEvent;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;
import org.mangorage.mboteventbus.impl.IEventBus;


@SuppressWarnings("unused")
public class BotEventListener {


    private final CorePlugin plugin;
    private final IEventBus bus;

    public BotEventListener(CorePlugin plugin) {
        this.plugin = plugin;
        this.bus = plugin.getPluginBus();
    }


    @SubscribeEvent
    public void messageRecieved(MessageReceivedEvent event) {
        var isCommand = Util.handleMessage(plugin, event);
        bus.post(new DMessageRecievedEvent(event, isCommand));
    }

    @SubscribeEvent
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        plugin.getCommandRegistry().postSlashCommand(new SlashCommandEvent(event, event.getName(), Arguments.empty()));
    }

    @SubscribeEvent
    public void sessionResumeEvent(SessionResumeEvent event) {
        LogHelper.info("Bot resumed Session...");
    }

    @SubscribeEvent
    public void sessionDisconnectEvent(SessionDisconnectEvent event) {
        LogHelper.info("Bot disconnected from Session...");
    }

    @SubscribeEvent
    public void messageReact(MessageReactionAddEvent event) {
        bus.post(new DReactionEvent(event));
    }

    @SubscribeEvent
    public void interact(ButtonInteractionEvent event) {
        bus.post(new DButtonInteractionEvent(event));
    }

    @SubscribeEvent
    public void messageUpdate(MessageUpdateEvent event) {
        bus.post(new DMessageUpdateEvent(event));
    }

    @SubscribeEvent
    public void messageDelete(MessageDeleteEvent event) {
        bus.post(new DMessageDeleteEvent(event));
    }

    @SubscribeEvent
    public void messageStringSelect(StringSelectInteractionEvent event) {
        bus.post(new DStringSelectInteractionEvent(event));
    }

    @SubscribeEvent
    public void voiceUpdate(GuildVoiceUpdateEvent event) {
        bus.post(new DVoiceUpdateEvent(event));
    }
}
