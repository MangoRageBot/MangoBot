package org.mangorage.mangobot.commands.trick.lua;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.mangorage.mangobot.MangoBot;
import org.mangorage.mangobot.commands.trick.Trick;
import org.mangorage.mangobot.commands.trick.TrickCommand;
import org.mangorage.mangobot.commands.trick.lua.builders.LuaEmbedBuilder;
import org.mangorage.mangobot.commands.trick.lua.builders.LuaMessageResponseBuilder;
import org.mangorage.mangobot.commands.trick.lua.helpers.LuaInfoHelper;
import org.mangorage.mangobot.commands.trick.lua.helpers.LuaObjectHelper;


public final class LuaJDA {
    private final MangoBot JDAPlugin;
    private final Message message;
    private final MessageChannel messageChannel;
    private final Trick trick;


    public LuaJDA(MangoBot plugin, Trick trick, Message message, MessageChannel channel) {
        this.JDAPlugin = plugin;
        this.message = message;
        this.messageChannel = channel;
        this.trick = trick;
    }

    public Object getStored(String key) {
        return trick.getMemoryBank().bank().get(key);
    }

    public Object getStoredOrSetAndGet(String key, Object value) {
        var result = getStored(key);
        if (result == null) {
            storeValue(key, value);
            return value;
        }
        return result;
    }

    public void storeValue(String key, Object o) {
        trick.getMemoryBank().bank().put(key, o);
        TrickCommand.TRICK_DATA_HANDLER.save(JDAPlugin.getPluginDirectory(), trick);
    }

    public LuaEmbedBuilder createEmbed() {
        return new LuaEmbedBuilder();
    }

    public LuaMessageResponseBuilder respond() {
        return new LuaMessageResponseBuilder(message.reply(""));
    }

    public LuaInfoHelper getInfoHelper() {
        return new LuaInfoHelper(message, messageChannel);
    }

    public LuaObjectHelper getObjectHelper() {
        return new LuaObjectHelper();
    }
}
