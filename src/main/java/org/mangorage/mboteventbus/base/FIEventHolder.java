/*
 * Copyright (c) 2023. MangoRage
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class FIEventHolder<T> {
    public static <X> FIEventHolder<X> create(Class<X> type, Function<FIEventListener<X>[], X> invokerFunction) {
        return new FIEventHolder<>(type, invokerFunction);
    }


    public record FIEventListener<T>(int priority, boolean recieveCancelled, T invoker) {
    }


    private final Function<FIEventListener<T>[], T> invokerFactory;
    protected volatile T invoker;
    private final Object lock = new Object();
    public final Class<T> classType;
    private FIEventListener<T>[] handlers;
    private int lastPriority = 0;

    @SuppressWarnings("unchecked")
    private FIEventHolder(Class<T> type, Function<FIEventListener<T>[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.classType = type;

        this.handlers = (FIEventListener<T>[]) Array.newInstance(FIEventListener.class, 0);
        update();
    }

    public T invoker() {
        return invoker;
    }

    protected FIEventListener<T>[] getHandlers() {
        return handlers;
    }

    public void addListener(T listener) {
        addListener(0, listener);
    }

    public void addListener(int priority, T listener) {
        addListener(priority, false, listener);
    }

    public void addListener(int priority, boolean recieveCancelled, T listener) {
        Objects.requireNonNull(listener, "Tried to register null Listener");

        synchronized (lock) {
            handlers = Arrays.copyOf(handlers, handlers.length + 1);
            handlers[handlers.length - 1] = new FIEventListener<>(priority, recieveCancelled, listener);
            if (lastPriority != priority)
                Arrays.sort(handlers, Comparator.<FIEventListener<T>>comparingInt(FIEventListener::priority).reversed());
            lastPriority = priority;
            update();
        }
    }

    public Class<T> getClassType() {
        return classType;
    }

    protected void update() {
        this.invoker = invokerFactory.apply(handlers);
    }
}
