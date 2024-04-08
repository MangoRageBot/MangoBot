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

package org.mangorage.jdautils;

import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.mangorage.jdautils.message.Filter;

public class MessageFilterEvent extends MessageReceivedEvent {

    private final Filter filter;

    private MessageFilterEvent(@NotNull Filter filter, @NotNull MessageReceivedEvent event) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());
        this.filter = filter;
    }

    @Contract("_, _ -> new")
    public static @NotNull MessageFilterEvent of(@NotNull Filter filter, @NotNull MessageReceivedEvent event) {
        Checks.notNull(filter, "Filter");
        Checks.notNull(event, "Event");
        return new MessageFilterEvent(filter, event);
    }

    public @NotNull String getMessageRaw() {
        return getMessage().getContentRaw();
    }

    /**
     * @return true if the message is from AutoMod
     */
    public boolean isFromAutoMod() {
        return isFromType(MessageType.AUTO_MODERATION_ACTION);
    }

    public Filter getFilter() {
        return filter;
    }

    public boolean isFromType(@NotNull MessageType type) {
        return getMessage().getType() == type;
    }
}
