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

package org.mangorage.mangobotapi.core.events;


import org.mangorage.eventbus.event.NormalGenericEvent;

public class DiscordEvent<T> extends NormalGenericEvent<T> {
    private final T instance;

    public DiscordEvent(Class<T> tClass, T instance) {
        super(tClass);
        this.instance = instance;
    }

    /**
     * Not ideal. Use {@link DiscordEvent#DiscordEvent(Class, Object)}  DiscordEvent}
     *
     * @param instance
     */
    @SuppressWarnings("unchecked")
    public DiscordEvent(T instance) {
        this((Class<T>) instance.getClass(), instance);
    }

    public T getInstance() {
        return instance;
    }
}
