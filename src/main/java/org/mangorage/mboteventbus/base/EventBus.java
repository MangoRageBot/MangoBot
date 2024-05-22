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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public final class EventBus implements IEventBus {
    public static IEventBus create() {
        return new EventBus();
    }

    private record ListenerKey(Class<?> eventClass, Class<?> genericClass) {
    }

    private final Map<ListenerKey, IListenerList<? extends Event>> LISTENERS = new ConcurrentHashMap<>();
    private final Semaphore writeLock = new Semaphore(1, true);

    public <T extends Event> void addListener(int priority, Class<T> eventClass, Consumer<T> eventConsumer) {
        IListenerList<T> list = getListenerList(eventClass, null);
        if (list == null) return;
        list.register(eventConsumer, "default -> " + eventClass, priority);
    }

    public <T extends GenericEvent<? extends G>, G> void addGenericListener(int priority, Class<G> genericClassFilter, Class<T> eventType, Consumer<T> genericEventListener) {
        IListenerList<T> list = getListenerList(eventType, genericClassFilter);
        if (list == null) return;
        list.register(genericEventListener, "default -> " + eventType + " -> " + genericClassFilter, priority);
    }

    public <T extends Event> void post(T event) {
        Class<?> genericType = null;
        if (event instanceof GenericEvent<?> genericEvent)
            genericType = genericEvent.getGenericType();

        writeLock.acquireUninterruptibly();
        IListenerList<T> listeners = getListenerList(getEventClass(event), genericType);
        writeLock.release();
        if (listeners != null) listeners.accept(event);
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> Class<T> getEventClass(T event) {
        return (Class<T>) event.getClass();
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> IListenerList<T> getListenerList(Class<T> eventClass, Class<?> genericClass) {
        var list = getListenerListInternal(eventClass, genericClass);
        return list == null ? null : (IListenerList<T>) list;
    }

    @SuppressWarnings("unchecked")
    private IListenerList<?> getListenerListInternal(Class<?> eventClass, Class<?> genericClass) {
        var key = new ListenerKey(eventClass, genericClass);
        var list = LISTENERS.get(key);
        if (list != null) return list;

        if (eventClass == Object.class || eventClass == Event.class && genericClass != null)
            return null;

        return LISTENERS.computeIfAbsent(key, e -> new ListenerListImpl<>(new CopyOnWriteArrayList<>(), (IListenerList<Event>) getListenerListInternal(eventClass.getSuperclass(), genericClass)));
    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }
}
