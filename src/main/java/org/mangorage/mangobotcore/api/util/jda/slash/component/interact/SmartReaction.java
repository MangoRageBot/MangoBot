/*
 * Copyright (c) 2024. MangoRage
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

package org.mangorage.mangobotcore.api.util.jda.slash.component.interact;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobotcore.api.util.jda.slash.command.watcher.EventWatcher;
import org.mangorage.mangobotcore.api.util.jda.slash.component.Component;
import org.mangorage.mangobotcore.api.util.jda.slash.component.NoRegistry;
import org.mangorage.mangobotcore.api.util.jda.slash.component.SendableComponent;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartReaction extends SendableComponent implements NoRegistry {

    private final List<Emoji> reactions;
    private EventWatcher<MessageReactionAddEvent> onAdd;
    private EventWatcher<MessageReactionRemoveEvent> onRemove;
    private String message;
    private MessageEmbed embed;

    private SmartReaction() {
        super("SmartReaction");
        reactions = new ArrayList<>();
    }

    @NotNull
    public static SmartReaction create(String message) {
        SmartReaction reaction = new SmartReaction();
        reaction.message = message;
        reaction.create();
        return reaction;
    }

    @NotNull
    public static SmartReaction create(MessageEmbed embed) {
        SmartReaction reaction = new SmartReaction();
        reaction.embed = embed;
        reaction.create();
        return reaction;
    }

    public SmartReaction addReaction(Emoji emoji) {
        reactions.add(emoji);
        return this;
    }

    public SmartReaction addReaction(String emoji) {
        reactions.add(Emoji.fromFormatted(emoji));
        return this;
    }

    public SmartReaction withListeners(EventWatcher.Listener<MessageReactionAddEvent> onAdd, EventWatcher.Listener<MessageReactionRemoveEvent> onRemove) {
        this.onAdd.setListener(event -> {
            EmojiUnion emoji = event.getEmoji();
            if (getReactions().contains(emoji))
                onAdd.onEvent(event);
            else
                event.getReaction().removeReaction(event.retrieveUser().complete()).queue();
        });
        this.onRemove.setListener(event -> {
            EmojiUnion emoji = event.getEmoji();
            if (getReactions().contains(emoji))
                onRemove.onEvent(event);
        });
        return this;
    }

    public SmartReaction withListeners(EventWatcher.Listener<MessageReactionAddEvent> onAdd, EventWatcher.Listener<MessageReactionRemoveEvent> onRemove, int expireAfter, TimeUnit unit) {
        this.onAdd.setListener(event -> {
            EmojiUnion emoji = event.getEmoji();
            if (getReactions().contains(emoji))
                onAdd.onEvent(event);
            else
                event.getReaction().removeReaction(event.retrieveUser().complete()).queue();
        }, expireAfter, unit);
        this.onRemove.setListener(event -> {
            EmojiUnion emoji = event.getEmoji();
            if (getReactions().contains(emoji))
                onRemove.onEvent(event);
        }, expireAfter, unit);
        return this;
    }

    protected void onCreate() {
        onAdd = new EventWatcher<>(this, MessageReactionAddEvent.class);
        onRemove = new EventWatcher<>(this, MessageReactionRemoveEvent.class);
    }

    protected void onRemove() {
        onAdd.destroy();
        onRemove.destroy();
    }

    protected MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        if (getReactions().isEmpty())
            throw new IllegalStateException("No reactions added to the SmartReaction");
        if (message != null)
            return event.getChannel().sendMessage(message);
        else
            return event.getChannel().sendMessageEmbeds(embed);
    }

    protected ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        if (getReactions().isEmpty())
            throw new IllegalStateException("No reactions added to the SmartReaction");
        if (message != null)
            return event.reply(message);
        else
            return event.replyEmbeds(embed);
    }

    protected void onSent(@NotNull Message message) {
        for (Emoji emoji : reactions)
            message.addReaction(emoji).queue();
    }

    protected List<Component> getChildren() {
        return null;
    }

    public List<Emoji> getReactions() {
        return reactions;
    }
}
