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

package org.mangorage.mangobotcore.api.util.jda.slash.command.watcher;


import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobotcore.api.util.jda.slash.command.CommandComponent;
import org.mangorage.mangobotcore.api.util.jda.slash.command.OptionComponent;
import org.mangorage.mangobotcore.api.util.jda.slash.component.SendableComponent;
import org.mangorage.mangobotcore.api.util.jda.slash.component.interact.SmartReaction;
import org.mangorage.mangobotcore.api.util.jda.slash.message.Filter;
import org.mangorage.mangobotcore.api.util.jda.slash.message.MessageComponent;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class WatcherManager {

    private static final List<EventWatcher<? extends Event>> watchers = new ArrayList<>();
    private static final List<EventWatcher<? extends Event>> pendingRemoval = new ArrayList<>();

    private WatcherManager() {
    }

    static void addWatcher(EventWatcher<? extends Event> watcher) {
        if (!watchers.contains(watcher))
            watchers.add(watcher);
    }

    static void removeWatcher(EventWatcher<? extends Event> watcher) {
        pendingRemoval.add(watcher);
    }

    public static void cleanup() {
        List<EventWatcher<? extends Event>> toRemove = new ArrayList<>(watchers.stream().filter(EventWatcher::expired).toList());
        toRemove.forEach(EventWatcher::destroy);

        watchers.removeAll(pendingRemoval);
        pendingRemoval.clear();
    }

    public static void cleanup(MessageDeleteEvent event) {
        List<SendableComponent> toRemove = new ArrayList<>();
        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof SendableComponent)
                .filter(watcher -> ((SendableComponent) watcher.getComponent()).isSent())
                .filter(watcher -> ((SendableComponent) watcher.getComponent()).getMessageId() == event.getMessageIdLong())
                .filter(watcher -> ((SendableComponent) watcher.getComponent()).getGuild().equals(event.getGuild()))
                .forEach(watcher -> toRemove.add((SendableComponent) watcher.getComponent()));
        toRemove.forEach(SendableComponent::remove);
        cleanup();
    }

    public static void onMessageEvent(@NotNull MessageReceivedEvent event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof MessageComponent)
                .forEach(watcher -> watcher.onEvent(event));
    }

    public static void onFilterTrigger(@NotNull List<Filter> triggeredFilters, MessageReceivedEvent event) {
        for (Filter triggeredFilter : triggeredFilters) {
            watchers.stream()
                    .filter(watcher -> watcher.getComponent() instanceof MessageComponent)
                    .filter(watcher -> ((MessageComponent) watcher.getComponent()).isFilter())
                    .filter(watcher -> Objects.equals(((MessageComponent) watcher.getComponent()).getFilterName(), triggeredFilter.getName()))
                    .forEach(watcher -> watcher.onEvent(MessageFilterEvent.of(triggeredFilter, event)));
        }
    }

    public static void onCommandEvent(@NotNull SlashCommandInteractionEvent event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof CommandComponent)
                .filter(watcher -> watcher.getComponent().getName().equals(event.getFullCommandName()) || ((CommandComponent) watcher.getComponent()).getAliases().contains(event.getFullCommandName()))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }

    public static void onCommandAutoCompleteEvent(@NotNull CommandAutoCompleteInteractionEvent event) {
        String commandAndOption = event.getFullCommandName() + " " + event.getFocusedOption().getName();

        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof OptionComponent)
                .filter(watcher -> watcher.getComponent().getName().equals(commandAndOption))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }

    public static void onContextEvent(GenericContextInteractionEvent<?> event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof CommandComponent)
                .filter(watcher -> watcher.getComponent().getName().equals(event.getName()))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }

    public static void onInteractionEvent(String componentID, Event event) {
        watchers.stream()
                .filter(watcher -> watcher.getComponent().getUuid() != null)
                .filter(watcher -> watcher.getComponent().getUuid().toString().equals(componentID))
                .findFirst()
                .ifPresent(watcher -> watcher.onEvent(event));
    }

    public static void onReactionEvent(@NotNull GenericMessageReactionEvent event) {
        if (event.retrieveUser().complete().isBot())
            return;

        watchers.stream()
                .filter(watcher -> watcher.getComponent() instanceof SmartReaction)
                .filter(watcher -> watcher.getComponent().getUuid() != null)
                .filter(watcher -> ((SmartReaction) watcher.getComponent()).getMessageId() == event.getMessageIdLong())
                .forEach(watcher -> watcher.onEvent(event));
    }

    public static String getStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("WatcherManager: ").append(watchers.size()).append(" watchers").append("\n");
        watchers.forEach(watcher -> {
            if (watcher.getComponent() instanceof CommandComponent cmd) {
                builder.append(cmd.isContextCommand() ? " - Context command: " : " - Slash command: ").append(watcher.getComponent().getName()).append("\n");
                return;
            }

            if (watcher.getComponent() instanceof MessageComponent msg) {
                builder.append(" - Message listener").append("\n");
                if (msg.getFilterName() != null)
                    builder.append("   - Filter: ").append(msg.getFilterName()).append("\n");
                return;
            }

            builder.append(" - ").append(watcher.getComponent().getName()).append("\n");
            if (watcher.getComponent().getIdentifier() != null)
                builder.append("   - IDENTIFIER: ").append(watcher.getComponent().getIdentifier()).append("\n");
            builder.append("   - UUID: ").append(watcher.getComponent().getUuid()).append("\n");
        });

        return builder.toString();
    }
}
