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

package org.mangorage.mangobot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.mangorage.mangobot.core.BotPermissions;
import org.mangorage.mangobot.core.EventListener;
import org.mangorage.mangobot.core.util.BotSettings;
import org.mangorage.mangobot.modules.basic.commands.HelpCommand;
import org.mangorage.mangobot.modules.basic.commands.InfoCommand;
import org.mangorage.mangobot.modules.basic.commands.PingCommand;
import org.mangorage.mangobot.modules.basic.commands.VersionCommand;
import org.mangorage.mangobot.modules.developer.SpeakCommand;
import org.mangorage.mangobot.modules.requestpaste.PasteRequestModule;
import org.mangorage.mangobot.modules.tricks.TrickCommand;
import org.mangorage.mangobotapi.core.events.SaveEvent;
import org.mangorage.mangobotapi.core.events.ShutdownEvent;
import org.mangorage.mangobotapi.core.events.StartupEvent;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;
import org.mangorage.mangobotapi.core.plugin.impl.IPlugin;
import org.mangorage.mangobotapi.core.plugin.impl.Plugin;
import org.mangorage.mboteventbus.EventBus;

import java.util.EnumSet;

@Plugin(id = Core.ID)
public class Core extends CorePlugin implements IPlugin {
    public static final String ID = "mangobot";


    private static final EnumSet<GatewayIntent> intents = EnumSet.of(
            // Enables MessageReceivedEvent for guild (also known as servers)
            GatewayIntent.GUILD_MESSAGES,
            // Enables the event for private channels (also known as direct messages)
            GatewayIntent.DIRECT_MESSAGES,
            // Enables access to message.getContentRaw()
            GatewayIntent.MESSAGE_CONTENT,
            // Enables MessageReactionAddEvent for guild
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            // Enables MessageReactionAddEvent for private channels
            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.SCHEDULED_EVENTS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES
    );

    private static final EnumSet<CacheFlag> cacheFlags = EnumSet.of(
            CacheFlag.EMOJI,
            CacheFlag.ROLE_TAGS,
            CacheFlag.VOICE_STATE,
            CacheFlag.ACTIVITY, // Cant do
            CacheFlag.CLIENT_STATUS,
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.STICKER,
            CacheFlag.SCHEDULED_EVENTS,
            CacheFlag.FORUM_TAGS
    );
    /*
                                    Activity.of(
                                        Activity.ActivityType.CUSTOM_STATUS,
                                        """
                                                    DM ME: !mail join to open a ticket!

                                                    MangoBot is on version %s"
                                                """.formatted(VersionCommand.getVersion()),
                                        "https://www.discord.minecraftforge.net/"
                                )
     */

    public Core() {
        super(
                JDABuilder.createDefault(BotSettings.BOT_TOKEN.get())
                        .setToken(BotSettings.BOT_TOKEN.get())
                        .setEnabledIntents(intents)
                        .enableCache(cacheFlags)
                        .setActivity(
                                Activity.of(
                                        Activity.ActivityType.CUSTOM_STATUS,
                                        """
                                                    MangoBot is on version %s"
                                                """.formatted(VersionCommand.getVersion()),
                                        "https://www.discord.minecraftforge.net/"
                                )
                        )
                        .setStatus(OnlineStatus.ONLINE)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .setEventManager(new AnnotatedEventManager())
                        .setEnableShutdownHook(true)
                        .setAutoReconnect(true),
                EventBus.create()
        );

        getJDA().addEventListener(new EventListener(this));
    }

    @Override
    public void load() {
        var registry = getCommandRegistry();

        registry.addBasicCommand(new VersionCommand(this));
        registry.addBasicCommand(new HelpCommand(this));
        registry.addBasicCommand(new InfoCommand(this));
        registry.addBasicCommand(new PingCommand());
        registry.addBasicCommand(new SpeakCommand(this));
        registry.addBasicCommand(new TrickCommand(this));

        PasteRequestModule.register(getPluginBus());
    }

    public void onStartup(StartupEvent event) {
        switch (event.phase()) {
            case STARTUP -> {
                BotPermissions.init();
            }
            case FINISHED -> {
            }
        }
    }

    @SuppressWarnings("all")
    public void onShutdown(ShutdownEvent event) {
        switch (event.phase()) {
            case PRE -> {
                getPluginBus().post(new SaveEvent());
            }
        }
    }

    /**
     * @return
     */
    @Override
    public String getId() {
        return Core.ID;
    }
}
