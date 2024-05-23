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

package org.mangorage.eventbus.impl;

import org.mangorage.basicutils.misc.Lazy;
import org.mangorage.eventbus.Listener;
import org.mangorage.eventbus.event.Event;
import org.mangorage.eventbus.interfaces.IListenerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ListenerListImpl<E extends Event> implements IListenerList<E> {
    private final List<Listener<E>> listeners = Collections.synchronizedList(new ArrayList<>());
    private final Set<IListenerList<E>> children = Collections.synchronizedSet(new CopyOnWriteArraySet<>());
    private final IListenerList<?> parent;
    private final Lazy<Listener<E>[]> allListeners;

    public ListenerListImpl(IListenerList<E> parent) {
        this.parent = parent;
        if (parent != null) parent.addChild(this);
        this.allListeners = Lazy.concurrentOf(() -> parent == null ? listeners.toArray(Listener[]::new) : Stream.of(listeners, List.of(parent.getListeners()))
                .flatMap(List::stream)
                .sorted()
                .toArray(Listener[]::new));
    }


    @Override
    public void register(int priority, Consumer<E> consumer) {
        listeners.add(new Listener<E>(priority, consumer));
        children.forEach(IListenerList::invalidate);
        if (parent != null) parent.invalidate();
    }

    /**
     * @return
     */
    @Override
    public Listener<E>[] getListeners() {
        return allListeners.get();
    }

    /**
     *
     */
    @Override
    public void invalidate() {
        allListeners.invalidate();
    }

    /**
     * @param child
     */
    @Override
    public void addChild(IListenerList<E> child) {
        children.add(child);
    }

    @Override
    public void post(E event) {
        for (Listener<E> listener : getListeners()) {
            listener.consumer().accept(event);
        }
    }
}
