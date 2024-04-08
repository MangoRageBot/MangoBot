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


import org.jetbrains.annotations.NotNull;
import org.mangorage.jdautils.EventWatcher;
import org.mangorage.jdautils.MessageFilterEvent;
import org.mangorage.jdautils.command.Unique;

public abstract class Filter {

    private final String name;
    private final String description;
    private final EventWatcher<MessageFilterEvent> watcher;

    protected Filter(String name, String description) {
        Unique.checkUnique("filter-name", name, "Filter name is not unique");
        this.name = name;
        this.description = description;

        watcher = new EventWatcher<>(new MessageComponent(this), MessageFilterEvent.class);
    }

    /**
     * Sets the listener for the filter, must be called if you want to set a custom action
     * <br>If the listener is not set, the filter will delete the message that triggered it.
     *
     * @param listener The listener
     */
    public final Filter withListener(@NotNull EventWatcher.Listener<MessageFilterEvent> listener) {
        watcher.setListener(listener);
        return this;
    }

    /**
     * Called when a message is received
     *
     * @param event The event
     * @return True if the filter triggered
     */
    protected abstract boolean onMessageReceived(@NotNull MessageFilterEvent event);

    protected final void destroy() {
        watcher.destroy();
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }
}
