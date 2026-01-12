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

package org.mangorage.mangobotcore.api.util.jda.slash.message;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangobotcore.api.util.jda.slash.command.watcher.EventWatcher;
import org.mangorage.mangobotcore.api.util.jda.slash.component.Component;
import org.mangorage.mangobotcore.api.util.jda.slash.component.NoRegistry;

public final class MessageComponent extends Component implements NoRegistry {

    private EventWatcher.Listener<MessageReceivedEvent> listener;
    private Filter filter;

    public MessageComponent(EventWatcher.Listener<MessageReceivedEvent> listener) {
        super("MessageComponent");
        this.listener = listener;
    }

    public MessageComponent(Filter filter) {
        super("MessageComponent");
        this.filter = filter;
    }

    public boolean isListener() {
        return listener != null;
    }

    public @Nullable EventWatcher.Listener<MessageReceivedEvent> getListener() {
        return listener;
    }

    public boolean isFilter() {
        return filter != null;
    }

    @Contract(pure = true)
    public @Nullable String getFilterName() {
        return isFilter() ? filter.getName() : null;
    }

    protected void onCreate() {
    }

    protected void onRemove() {
    }
}
