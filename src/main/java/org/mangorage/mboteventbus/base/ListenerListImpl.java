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

import org.mangorage.basicutils.misc.Lazy;
import org.mangorage.mboteventbus.impl.IListenerList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ListenerListImpl<T> implements IListenerList<T> {
    private final List<Listener<T>> backingList;
    private final IListenerList<?> parent;
    private final Set<IListenerList<?>> children = new HashSet<>();
    private final Lazy<Listener<T>[]> listeners;

    public ListenerListImpl(List<Listener<T>> backingList, IListenerList<?> parent) {
        this.backingList = backingList;
        this.parent = parent;
        if (parent != null) parent.addChild(this);

        this.listeners = Lazy.concurrentOf(() -> {
            if (parent != null) {
                return Stream.of(backingList, List.of(parent.getListeners()))
                        .flatMap(List::stream)
                        .sorted()
                        .toArray(Listener[]::new);
            }

            return backingList.toArray(Listener[]::new);
        });
    }

    /**
     * @param child
     */
    @Override
    public void addChild(IListenerList<?> child) {
        children.add(child);
    }

    /**
     * @param event
     */
    @Override
    public void accept(Event event) {
        for (Listener<T> listener : getListeners()) {
            listener.consumer().accept((T) event);
        }
    }

    /**
     * @param eventConsumer
     * @param name
     * @param priority
     */
    @Override
    public void register(Consumer<T> eventConsumer, String name, int priority) {
        backingList.add(new Listener<>(priority, name, eventConsumer));
        if (parent != null) parent.invalidate();
        children.forEach(IListenerList::invalidate);
    }

    /**
     * @return
     */
    @Override
    public Listener<T>[] getListeners() {
        return listeners.get();
    }

    /**
     *
     */
    @Override
    public void invalidate() {
        listeners.invalidate();
    }


}
