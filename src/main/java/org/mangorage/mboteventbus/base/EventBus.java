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
import org.mangorage.mboteventbus.impl.IListenerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class EventBus implements IEventBus {
    public static IEventBus create() {
        return new EventBus();
    }

    private record ListenerKey(Class<?> eventClass, Class<?> genericClass) {
    }

    private final Map<ListenerKey, IListenerList<? extends Event>> LISTENERS = new HashMap<>();

    public <T extends Event> void addListener(int priority, Class<T> eventClass, Consumer<T> eventConsumer) {
        IListenerList<T> list = (IListenerList<T>) getListenerList(eventClass, null);
        list.register(eventConsumer, "default", priority);
    }

    public <G, T extends GenericEvent<G>> void addGenericListener(int priority, Class<G> genericClass, Class<T> genericEvent, Consumer<T> genericEventListener) {
        IListenerList<T> list = (IListenerList<T>) getListenerList(genericEvent, genericClass);
        list.register(genericEventListener, "default", priority);
    }

    public void post(Event event) {
        Class<?> genericType = null;
        if (event instanceof GenericEvent<?> genericEvent)
            genericType = genericEvent.getGenericType();

        var listeners = getListenerList(event.getClass(), genericType);
        if (listeners != null)
            listeners.accept(event);
    }

    @SuppressWarnings("unchecked")
    private IListenerList<?> getListenerList(Class<?> eventClass, Class<?> genericClass) {
        if (genericClass != null) {
            return LISTENERS.computeIfAbsent(new ListenerKey(eventClass, genericClass), e -> new ListenerListImpl<Event>(new ArrayList<>(), null));
        }

        var list = LISTENERS.get(eventClass);
        if (list != null) {
            return list;
        }

        if (eventClass == Object.class)
            return null;

        return LISTENERS.computeIfAbsent(new ListenerKey(eventClass, null), e -> new ListenerListImpl<>(new ArrayList<>(), getListenerList(eventClass.getSuperclass(), null)));
    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }


    public static class MyEvent extends Event {
    }

    public static void main(String[] args) {
        IEventBus bus = create();

        bus.addListener(10, Event.class, e -> {
            System.out.println(e.getClass());
        });

        bus.post(new Event());
        bus.post(new MyEvent());
    }
}
