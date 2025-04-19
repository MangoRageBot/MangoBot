package org.mangorage.mangobot.commands.trick.lua.builders;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.Arrays;
import java.util.List;

public class LuaMessageResponseBuilder {
    private final MessageCreateAction message;
    public LuaMessageResponseBuilder(MessageCreateAction message) {
        this.message = message;
    }

    public LuaMessageResponseBuilder reply(String content) {
        message.setContent(content);
        return this;
    }

    public LuaMessageResponseBuilder setMentions(String name) {
        String[] names = name.split(":");
        if (names.length == 0 || names[0].isEmpty()) {
            message.setAllowedMentions(List.of());
        } else {
            message.setAllowedMentions(
                    Arrays.stream(names)
                            .map(Message.MentionType::valueOf)
                            .filter(t -> {
                                if (t == Message.MentionType.EVERYONE) return false;
                                if (t == Message.MentionType.HERE) return  false;
                                return true;
                            })
                            .toList()
            );
        }
        return this;
    }

    public LuaMessageResponseBuilder setSuppressedNotifications(boolean value) {
        message.setSuppressedNotifications(value);
        return this;
    }

    public LuaMessageResponseBuilder setMentionsUser(boolean mentionsUser) {
        message.mentionRepliedUser(mentionsUser);
        return this;
    }

    public LuaMessageResponseBuilder setMention(String user) {
        String[] users = user.split(":");
        message.mentionUsers(users);
        return this;
    }

    public LuaMessageResponseBuilder setEmbed(MessageEmbed embed) {
        message.setEmbeds(embed);
        return this;
    }

    public void queue() {
        message.setAllowedMentions(
                message.getAllowedMentions()
                        .stream()
                        .filter(t -> {
                            if (t == Message.MentionType.EVERYONE) return false;
                            if (t == Message.MentionType.HERE) return  false;
                            return true;
                        })
                        .toList()
        ).queue();
    }
}
