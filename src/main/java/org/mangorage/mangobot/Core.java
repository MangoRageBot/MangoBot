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
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.mangorage.basicutils.config.Config;
import org.mangorage.basicutils.config.ConfigSetting;
import org.mangorage.basicutils.config.ISetting;
import org.mangorage.mangobot.core.BotEventListener;
import org.mangorage.mangobot.core.BotPermissions;
import org.mangorage.mangobot.core.Listeners;
import org.mangorage.mangobot.modules.basic.commands.GetEmbedsCommand;
import org.mangorage.mangobot.modules.basic.commands.HelpCommand;
import org.mangorage.mangobot.modules.basic.commands.InfoCommand;
import org.mangorage.mangobot.modules.basic.commands.JoinCommand;
import org.mangorage.mangobot.modules.basic.commands.LeaveCommand;
import org.mangorage.mangobot.modules.basic.commands.PermissionCommand;
import org.mangorage.mangobot.modules.basic.commands.PingCommand;
import org.mangorage.mangobot.modules.basic.commands.PrefixCommand;
import org.mangorage.mangobot.modules.basic.commands.VersionCommand;
import org.mangorage.mangobot.modules.developer.KickBotCommand;
import org.mangorage.mangobot.modules.developer.RestartCommand;
import org.mangorage.mangobot.modules.developer.RunCode;
import org.mangorage.mangobot.modules.developer.SpeakCommand;
import org.mangorage.mangobot.modules.developer.TerminateCommand;
import org.mangorage.mangobot.modules.mappings.ClassMapCommand;
import org.mangorage.mangobot.modules.mappings.DefMapCommand;
import org.mangorage.mangobot.modules.mappings.FCICommand;
import org.mangorage.mangobot.modules.mappings.MCPCommand;
import org.mangorage.mangobot.modules.mappings.MappingsMainCommand;
import org.mangorage.mangobot.modules.mappings.MappingsManager;
import org.mangorage.mangobot.modules.mappings.VarMapCommand;
import org.mangorage.mangobot.modules.mappings.YCCommand;
import org.mangorage.mangobot.modules.mappings.YFCommand;
import org.mangorage.mangobot.modules.mappings.YMCommand;
import org.mangorage.mangobot.modules.music.commands.PauseCommand;
import org.mangorage.mangobot.modules.music.commands.PlayCommand;
import org.mangorage.mangobot.modules.music.commands.PlayingCommand;
import org.mangorage.mangobot.modules.music.commands.QueueCommand;
import org.mangorage.mangobot.modules.music.commands.StopCommand;
import org.mangorage.mangobot.modules.music.commands.VolumeCommand;
import org.mangorage.mangobot.modules.requestpaste.PasteRequestModule;
import org.mangorage.mangobot.modules.translate.Translate;
import org.mangorage.mangobot.modules.tricks.TrickCommand;
import org.mangorage.mangobotapi.core.events.LoadEvent;
import org.mangorage.mangobotapi.core.events.SaveEvent;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;
import org.mangorage.mangobotapi.core.plugin.impl.Plugin;

import java.util.EnumSet;

import static org.mangorage.mangobot.core.BotPermissions.*;

@Plugin(id = Core.ID)
public class Core extends CorePlugin {
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
            CacheFlag.ACTIVITY,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.STICKER,
            CacheFlag.SCHEDULED_EVENTS,
            CacheFlag.FORUM_TAGS
    );

    // Where we create our "config"
    private final static Config CONFIG = new Config("plugins/%s/".formatted(Core.ID), ".env");


    // Where we create Settings for said Config
    public static final ISetting<String> MAPPINGS_VERSION = ConfigSetting.create(CONFIG, "MAPPINGS_VERSION", "empty");
    public static final ISetting<String> BOT_TOKEN = ConfigSetting.create(CONFIG, "BOT_TOKEN", "empty");
    public static final ISetting<String> PASTE_TOKEN = ConfigSetting.create(CONFIG, "PASTE_TOKEN", "empty");
    public static final ISetting<String> DEEPL_TOKEN = ConfigSetting.create(CONFIG, "DEEPL_TOKEN", "empty");


    private Translate translate = new Translate(this);

    public Core() {
        super(
                Core.ID,
                JDABuilder.createDefault(BOT_TOKEN.get())
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
                        .setAutoReconnect(true)
                        .build()
        );

        // translate.register();

        getJDA().addEventListener(new BotEventListener(this));
    }

    public static class Test implements EventListener {

        /**
         * @param event The Event to handle.
         */
        @Override
        public void onEvent(@NotNull GenericEvent event) {
            System.out.println(event);
        }
    }

    @Override
    public void startup() {
        BotPermissions.init();
        getPluginBus().register(new Listeners(this));
    }


    @Override
    public void registration() {
        var cmdRegistry = getCommandRegistry();
        var permRegistry = getPermissionRegistry();

        permRegistry.register(PLAYING);
        permRegistry.register(TRICK_ADMIN);
        permRegistry.register(PREFIX_ADMIN);
        permRegistry.register(MOD_MAIL);
        permRegistry.register(PERMISSION_ADMIN);
        permRegistry.register(CUSTOM_VC_ADMIN);


        // Basic Commands
        cmdRegistry.addBasicCommand(new HelpCommand(this));
        cmdRegistry.addBasicCommand(new InfoCommand(this));
        cmdRegistry.addBasicCommand(new JoinCommand());
        cmdRegistry.addBasicCommand(new LeaveCommand());
        cmdRegistry.addBasicCommand(new PermissionCommand(this));
        cmdRegistry.addBasicCommand(new PingCommand());
        cmdRegistry.addBasicCommand(new PrefixCommand(this));
        cmdRegistry.addBasicCommand(new VersionCommand(this));


        // Developer Commands
        cmdRegistry.addBasicCommand(new KickBotCommand(this));
        cmdRegistry.addBasicCommand(new RestartCommand());
        cmdRegistry.addBasicCommand(new SpeakCommand(this));
        cmdRegistry.addBasicCommand(new TerminateCommand());


        // Music Commands
        cmdRegistry.addBasicCommand(new PlayCommand());
        cmdRegistry.addBasicCommand(new PauseCommand());
        cmdRegistry.addBasicCommand(new PlayingCommand());
        cmdRegistry.addBasicCommand(new QueueCommand());
        cmdRegistry.addBasicCommand(new StopCommand());
        cmdRegistry.addBasicCommand(new VolumeCommand());


        // Tricks
        cmdRegistry.addBasicCommand(new TrickCommand(this));

        // Mappings
        MappingsManager latest_mappings_manager = MappingsManager.new_(); // Soon we need to do for multiple versions but not ATM
        latest_mappings_manager.init(MAPPINGS_VERSION);
        cmdRegistry.addBasicCommand(new MappingsMainCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new ClassMapCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new DefMapCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new VarMapCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new MCPCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new FCICommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new YCCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new YMCommand(latest_mappings_manager,this));
        cmdRegistry.addBasicCommand(new YFCommand(latest_mappings_manager,this));

        
        // Test
        cmdRegistry.addBasicCommand(new RunCode());
        cmdRegistry.addBasicCommand(new GetEmbedsCommand());


        permRegistry.save();
        PasteRequestModule.register(getPluginBus());
    }

    @Override
    public void finished() {
        getPluginBus().post(new LoadEvent());
    }

    @Override
    public void shutdownPre() {
        getPluginBus().post(new SaveEvent());
    }


    @Override
    public void shutdownPost() {

    }
}
