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

package org.mangorage.jdautils.message;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.mangorage.jdautils.MessageFilterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageFilter {

    private static final Map<Long, MessageFilter> filters = new ConcurrentHashMap<>();

    /**
     * Gets or creates the message filter for the specified guild
     *
     * @param guild The guild to get the message filter for, must not be null
     * @return The message filter for the specified guild
     */
    public static MessageFilter get(@NotNull Guild guild) {
        Checks.notNull(guild, "Guild");
        return filters.computeIfAbsent(guild.getIdLong(), guildID -> new MessageFilter(guild));
    }

    /**
     * Destroys all active message filters
     */
    public static void destroyAll() {
        filters.values().forEach(MessageFilter::destroy);
        filters.clear();
    }

    /**
     * Broadcasts the specified event to all active filters in the specified guild
     * <br><b>Internal use only!
     *
     * @param event The event to broadcast, must not be null
     * @return A list of all filters that triggered
     */
    public static @NotNull List<Filter> broadcastEvent(@NotNull MessageReceivedEvent event) {
        Checks.notNull(event, "Event");

        List<Filter> triggeredFilters = new ArrayList<>();
        filters.values()
                .stream()
                .filter(filter -> filter.guild.getIdLong() == event.getGuild().getIdLong())
                .forEach(filter -> triggeredFilters.addAll(filter.onMessageReceived(event)));

        return triggeredFilters;
    }

    private final Guild guild;
    private final List<Filter> messageFilters = new ArrayList<>();

    private MessageFilter(@NotNull Guild guild) {
        this.guild = guild;
    }

    private @NotNull List<Filter> onMessageReceived(@NotNull MessageReceivedEvent event) {
        List<Filter> triggeredFilters = new ArrayList<>();
        messageFilters.forEach(filter -> {
            if (filter.onMessageReceived(MessageFilterEvent.of(filter, event)) && !triggeredFilters.contains(filter))
                triggeredFilters.add(filter);
        });
        return triggeredFilters;
    }

    /**
     * Destroys the message filter and clears all active filters
     */
    public void destroy() {
        messageFilters.forEach(Filter::destroy);
        messageFilters.clear();
        filters.remove(guild.getIdLong());
    }

    /**
     * Adds the specified filter to the message filter
     *
     * @param filter The filter to add, must not be null
     */
    public void addFilter(@NotNull Filter filter) {
        Checks.notNull(filter, "Filter");
        if (!messageFilters.contains(filter))
            messageFilters.add(filter);
    }

    /**
     * Removes the specified filter from the message filter
     *
     * @param filterName The name of the filter to remove, must not be null
     */
    public void removeFilter(@NotNull String filterName) {
        Checks.notNull(filterName, "Filter name");
        messageFilters.removeIf(filter -> filter.getName().equalsIgnoreCase(filterName));
    }

    public List<Filter> getMessageFilters() {
        return messageFilters;
    }

    public Guild getGuild() {
        return guild;
    }
}
