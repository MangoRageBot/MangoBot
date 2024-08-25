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

import org.mangorage.eventbus.interfaces.IEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class ListenerList<E extends IEvent> {
    private final List<EventListener<E>> listeners = Collections.synchronizedList(new ArrayList<>(2));
    private final List<ListenerList<E>> children = Collections.synchronizedList(new ArrayList<>(2));
    private final ListenerList<?> parent;

    private final AtomicReference<EventListener<E>[]> allListeners = new AtomicReference<>();
    private final Semaphore writeLock = new Semaphore(1, true);
    private boolean rebuild = true;

    public ListenerList(ListenerList<E> parent) {
        this.parent = parent;
        if (parent != null) parent.addChild(this);
    }

    @SuppressWarnings("unchecked")
    private List<EventListener<E>> getListenersInternal() {
        writeLock.acquireUninterruptibly();
        ArrayList<EventListener<E>> list = new ArrayList<>(listeners);
        writeLock.release();
        if (parent != null) {
            list.addAll(((ListenerList<E>) parent).getListenersInternal());
        }
        return list;
    }

    private EventListener<E>[] getListeners() {
        if (shouldRebuild()) buildCache();
        return allListeners.get();
    }

    private void forceRebuild() {
        this.rebuild = true;
        synchronized (this.children) {
            for (ListenerList<E> child : children) {
                child.forceRebuild();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void buildCache() {
        if (parent != null && parent.shouldRebuild()) {
            parent.buildCache();
        }

        allListeners.set(
                getListenersInternal()
                        .stream()
                        .sorted()
                        .toArray(EventListener[]::new)
        );

        rebuild = false;
    }

    private boolean shouldRebuild() {
        return rebuild;
    }

    private void addChild(ListenerList<E> child) {
        children.add(child);
    }

    public void post(E event) {
        for (EventListener<E> listener : getListeners()) {
            listener.consumer().accept(event);
        }
    }

    public void register(int priority, Consumer<E> consumer) {
        writeLock.acquireUninterruptibly();
        listeners.add(new EventListener<E>(priority, consumer));
        writeLock.release();
        children.forEach(ListenerList::forceRebuild);
        if (parent != null) parent.forceRebuild();
    }
}
