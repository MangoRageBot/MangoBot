package org.mangorage.mangobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.mangorage.commonutils.config.Config;
import org.mangorage.commonutils.config.ConfigSetting;
import org.mangorage.commonutils.config.ISetting;
import org.mangorage.mangobot.commands.PingCommand;
import org.mangorage.mangobotcore.jda.command.api.CommandManager;
import org.mangorage.mangobotcore.plugin.api.MangoBotPlugin;
import org.mangorage.mangobotcore.plugin.api.Plugin;

import java.nio.file.Path;
import java.util.EnumSet;

@MangoBotPlugin(id = MangoBot.ID)
public final class MangoBot implements Plugin {
    public static final String ID = "mangobot";

    // Where we create our "config"
    public final static Config CONFIG = new Config(Path.of("plugins/%s/.env".formatted(MangoBot.ID)));

    // Where we create Settings for said Config
    public static final ISetting<String> BOT_TOKEN = ConfigSetting.create(CONFIG, "BOT_TOKEN", "empty");

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
    private final CommandManager commandManager = CommandManager.create();

    private JDA jda;

    public MangoBot() {
        commandManager.register(new PingCommand());
    }

    public void load() {
        jda = JDABuilder.createDefault(BOT_TOKEN.get())
                .setEnabledIntents(intents)
                .enableCache(cacheFlags)
                .setActivity(
                        Activity.of(
                                Activity.ActivityType.WATCHING,
                                "https://mangobot.mangorage.org/"
                        )
                )
                .setStatus(OnlineStatus.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEventManager(new AnnotatedEventManager())
                .setEnableShutdownHook(true)
                .setAutoReconnect(true)
                .build();

        getJDA().addEventListener(new BotEventListener(this));
    }

    public JDA getJDA() {
        return jda;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
