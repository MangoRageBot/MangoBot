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
import org.mangorage.eventbus.annotations.SubscribeEvent;
import org.mangorage.eventbus.event.core.Event;
import org.mangorage.eventbus.event.core.GenericEvent;
import org.mangorage.eventbus.interfaces.IEvent;
import org.mangorage.eventbus.interfaces.IEventBus;
import org.mangorage.eventbus.interfaces.IEventType;
import org.mangorage.eventbus.interfaces.IEventTypeHandler;
import org.mangorage.eventbus.interfaces.IGenericEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Consumer;

public final class EventBus<EE extends IEvent & IEventType<F>, GG extends IGenericEvent<?> & IEventType<F>, F extends IEventType<F>> implements IEventBus<F> {

    public static <E extends IEvent & IEventType<F>, G extends IGenericEvent<?> & IEventType<F>, F extends IEventType<F>> IEventBus<F> create(IEventTypeHandler<E, G, F> handler, Class<F> flagType) {
        return new EventBus<>(handler, flagType);
    }

    private final Map<EventKey<?, ?>, ListenerList<?>> LISTENERS = new Object2ObjectArrayMap<>();
    private final IEventTypeHandler<EE, GG, F> handler;
    private final Class<F> flagType;

    private EventBus(IEventTypeHandler<EE, GG, F> handler, Class<F> flagType) {
        this.handler = handler;
        this.flagType = flagType;
    }


    @Override
    public <E extends IEvent & IEventType<F>> void addListener(int priority, Class<E> eventClass, Consumer<E> consumer) {
        var list = getListenerList(eventClass, null);
        if (list != null) list.register(priority, consumer);
    }

    @Override
    public <E extends IEvent & IGenericEvent<G> & IEventType<F>, G> void addGenericListener(int priority, Class<G> baseFilterClass, Class<E> eventClass, Consumer<E> consumer) {
        var list = getListenerList(eventClass, baseFilterClass);
        if (list != null) list.register(priority, consumer);
    }


    private <E extends IEvent & IGenericEvent<G> & IEventType<F>, G> void addGenericListenerCast(int priority, Class<G> baseFilterClass, Class<?> eventClass, Consumer<?> consumer) {
        addGenericListener(
                priority,
                baseFilterClass,
                (Class<E>) eventClass,
                (Consumer<E>) consumer
        );
    }


    @Override
    public void registerClass(Class<?> clazz) {
        register(clazz, null);
    }

    @Override
    public void registerObject(Object object) {
        register(object.getClass(), object);
    }

    @SuppressWarnings("unchecked")
    private void register(Class<?> clazz, Object instance) {
        // TODO: FIX LATER
        for (Method method : clazz.getDeclaredMethods()) {
            var subscribeEvent = method.getDeclaredAnnotation(SubscribeEvent.class);

            if (subscribeEvent == null) continue;

            // Don't register Static Methods if we are registering an Object,
            // Don't register non-static Methods if we are registering a Class
            if (Modifier.isStatic(method.getModifiers()) && instance != null) continue;
            if (!Modifier.isStatic(method.getModifiers()) && instance == null) continue;

            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalStateException("Cannot register an EventListener that isnt set to Public!");
            }

            var params = method.getParameters();
            if (params.length != 1) {
                throw new IllegalStateException("Tried to register an EventListener with more then one parameter.");
            }
            if (!Event.class.isAssignableFrom(params[0].getType())) {
                throw new IllegalStateException("Tried to register an EventListener with a Non Event type Parameter. Got -> " + params[0].getType());
            }

            var eventClass = (Class<Event>) params[0].getType();
            if (handler.canHandle(eventClass, ListenerType.GENERIC)) {
                var genericType = subscribeEvent.genericType();
                var genericClass = handler.castGenericClass(params[0].getType(), genericType);
                if (genericType == SubscribeEvent.NullClass.class) {
                    throw new IllegalStateException("Cannot Register a GenericListener due to no GenericType being defined...");
                }

                Consumer<GenericEvent<Object>> consumer = e -> {
                    try {
                        method.invoke(instance, e);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                };

                addGenericListenerCast(
                        subscribeEvent.priority(),
                        (Class<Object>) genericType,
                        genericClass,
                        consumer
                );
            } else if (handler.canHandle(eventClass, ListenerType.NORMAL)) {
                Consumer<Event> consumer = e -> {
                    try {
                        method.invoke(instance, e);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                };

                addListener(
                        subscribeEvent.priority(),
                        handler.castNormalClass(eventClass),
                        handler.castNormalConsumer(consumer)
                );
            } else {
                throw new IllegalStateException("Cannot register Method %s with EventType of %s needs to implement %s".formatted(method, eventClass, flagType));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends IEvent> ListenerList<E> getListenerList(Class<E> eventClass, Class<?> genericType) {
        var key = new EventKey<>(eventClass, genericType);
        var list = LISTENERS.get(key);

        if (list != null) return (ListenerList<E>) list;
        if (((Class<?>) eventClass) == Object.class || !flagType.isAssignableFrom(eventClass)) return null;

        // Cant store listeners for a Generic Event if there is no genericType!!
        if (genericType == null && IGenericEvent.class.isAssignableFrom(eventClass)) return null;

        synchronized (this) {
            return (ListenerList<E>) LISTENERS.computeIfAbsent(key, e -> new ListenerList<>(getListenerList((Class<E>) eventClass.getSuperclass(), genericType)));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends IEvent & IEventType<F>> void post(E event) {
        Class<?> genericType = null;
        if (event instanceof IGenericEvent<?> genericEvent)
            genericType = genericEvent.getGenericType();

        ListenerList<E> list = (ListenerList<E>) getListenerList(event.getClass(), genericType);
        if (list != null) list.post(event);
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
