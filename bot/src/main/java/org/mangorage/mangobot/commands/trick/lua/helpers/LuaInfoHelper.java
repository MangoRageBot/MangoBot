package org.mangorage.mangobot.commands.trick.lua.helpers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class LuaInfoHelper {
    private final Message message;
    private final MessageChannel channel;

    public LuaInfoHelper(Message message, MessageChannel channel) {
        this.message = message;
        this.channel = channel;
    }

    public long getAuthorId() {
        return message.getAuthor().getIdLong();
    }

    public String getAuthorIdString() {
        return message.getAuthor().getId();
    }
}
