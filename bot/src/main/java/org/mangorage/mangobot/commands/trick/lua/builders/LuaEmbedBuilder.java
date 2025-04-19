package org.mangorage.mangobot.commands.trick.lua.builders;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LuaEmbedBuilder {
    private final EmbedBuilder builder = new EmbedBuilder();

    public LuaEmbedBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public LuaEmbedBuilder setTitle(String title, String url) {
        builder.setTitle(title, url);
        return this;
    }

    public LuaEmbedBuilder setDescription(String description) {
        builder.setDescription(description);
        return this;
    }

    public EmbedBuilder getBuilder() {
        return builder;
    }

    public MessageEmbed build() {
        return builder.build();
    }
}
