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

package org.mangorage.mboteventbus.base;

import org.mangorage.mboteventbus.impl.IEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class EventBus implements IEventBus {
    public static IEventBus create() {
        return new EventBus();
    }

    public record ListenerKey(Class<?> eventClass, Class<?> genericClass) {
    }

    private final Map<ListenerKey, List<Consumer<? extends Event>>> LISTENERS = new HashMap<>();

    public <T extends Event> void addListener(int priority, Class<T> eventClass, Consumer<T> eventConsumer) {
        LISTENERS.computeIfAbsent(new ListenerKey(eventClass, null), a -> new ArrayList<>()).add(eventConsumer);
    }

    public <G, T extends GenericEvent<G>> void addGenericListener(int priority, Class<G> genericClass, Class<T> genericEvent, Consumer<T> genericEventListener) {
        LISTENERS.computeIfAbsent(new ListenerKey(genericEvent, genericClass), a -> new ArrayList<>()).add(genericEventListener);
    }

    public void post(Event event) {
        Class<?> genericType = null;
        if (event instanceof GenericEvent<?> genericEvent)
            genericType = genericEvent.getGenericType();

        var listeners = LISTENERS.get(new ListenerKey(event.getClass(), genericType));
        if (listeners != null)
            listeners.forEach(l -> {
                @SuppressWarnings("unchecked")
                Consumer<Event> listener = (Consumer<Event>) l;
                listener.accept(event);
            });

    }

    /**
     *
     */
    @Override
    public void startup() {

    }

    /**
     *
     */
    @Override
    public void shutdown() {

    }

    public static void main(String[] args) {
        EventBus bus = new EventBus();
    }
}
