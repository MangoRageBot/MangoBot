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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventBus implements IEventBus {
    public static IEventBus create() {
        return new EventBus();
    }

    private record ListenerKey(Class<?> eventClass, Class<?> genericClass) {
    }

    private final Map<ListenerKey, IListenerList<? extends Event>> LISTENERS = new ConcurrentHashMap<>();


    @SuppressWarnings("unchecked")
    public <T extends Event> void addListener(int priority, Class<T> eventClass, Consumer<T> eventConsumer) {
        IListenerList<T> list = (IListenerList<T>) getListenerList(eventClass, null);
        if (list == null) return;
        list.register(eventConsumer, "default", priority);
    }


    @SuppressWarnings("unchecked")
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(int priority, Class<G> genericClassFilter, Class<T> eventType, Consumer<T> genericEventListener) {
        IListenerList<T> list = (IListenerList<T>) getListenerList(eventType, genericClassFilter);
        if (list == null) return;
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

    private IListenerList<?> getListenerList(Class<?> eventClass, Class<?> genericClass) {
        var key = new ListenerKey(eventClass, genericClass);
        var list = LISTENERS.get(key);
        if (list != null) {
            return list;
        }

        if (eventClass == Object.class)
            return null;

        if (eventClass == Event.class && genericClass != null)
            return null;

        return LISTENERS.computeIfAbsent(key, e -> new ListenerListImpl<>(new ArrayList<>(), getListenerList(eventClass.getSuperclass(), genericClass)));
    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }


    public static class MyEvent extends Event {
        public MyEvent() {
            //super(Integer.class);
        }
    }

    public static class MyBetterEvent extends MyEvent {
    }

    public static class MyCrazyEvent extends MyBetterEvent {
    }


    public static void main(String[] args) {
        IEventBus bus = create();


//        bus.addGenericListener(10, Integer.class, MyEvent.class, e -> {
//            System.out.println("Sweet -> " + e.getClass());
//        });
//
        bus.addGenericListener(10, Integer.class, GenericEvent.class, e -> {
            System.out.println("Generic -> " + e.getClass());

        });

        bus.addListener(10, Event.class, e -> {
            System.out.println(e.getClass());
        });

        bus.addListener(10, MyEvent.class, e -> {
            System.out.println("Sweeet! -> " + e.getClass());
        });

        bus.post(new MyBetterEvent());
        bus.post(new MyEvent());
        bus.post(new MyCrazyEvent());
        bus.post(new GenericEvent<>(Integer.class) {
        });

        System.out.println("END");
    }
}
