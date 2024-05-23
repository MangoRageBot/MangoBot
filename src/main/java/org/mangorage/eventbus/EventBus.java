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

package org.mangorage.eventbus;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.mangorage.eventbus.event.Event;
import org.mangorage.eventbus.interfaces.IEventBus;
import org.mangorage.eventbus.interfaces.IGenericEvent;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public final class EventBus implements IEventBus {

    public static IEventBus create() {
        return new EventBus();
    }


    private final Map<EventKey<?, ?>, ListenerList<?>> LISTENERS = new Object2ObjectArrayMap<>();
    private final Semaphore writeLock = new Semaphore(1, true);

    private EventBus() {
    }


    @Override
    public <E extends Event> void addListener(int priority, Class<E> eventClass, Consumer<E> consumer) {
        getListenerList(eventClass, null).register(priority, consumer);
    }


    @Override
    public <E extends Event & IGenericEvent<G>, G> void addGenericListener(int priority, Class<G> baseFilterClass, Class<E> eventClass, Consumer<E> consumer) {
        getListenerList(eventClass, baseFilterClass).register(priority, consumer);
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> ListenerList<E> getListenerList(Class<E> eventClass, Class<?> genericType) {
        var key = new EventKey<>(eventClass, genericType);
        var list = LISTENERS.get(key);

        if (list != null) return (ListenerList<E>) list;
        if (((Class<?>) eventClass) == Object.class) return null;

        synchronized (this) {
            return (ListenerList<E>) LISTENERS.computeIfAbsent(key, e -> new ListenerList<>(getListenerList((Class<E>) eventClass.getSuperclass(), genericType)));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Event> void post(E event) {
        Class<?> genericType = null;
        if (event instanceof IGenericEvent<?> genericEvent)
            genericType = genericEvent.getGenericType();

        ListenerList<E> list = (ListenerList<E>) getListenerList(event.getClass(), genericType);
        list.post(event);
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
}
