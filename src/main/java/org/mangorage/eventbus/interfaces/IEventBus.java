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

package org.mangorage.eventbus.interfaces;

import org.mangorage.eventbus.event.core.Event;

import java.util.function.Consumer;

public interface IEventBus<F extends IEventType<F>> {
    <E extends Event & IEventType<F>> void addListener(int priority, Class<E> eventClass, Consumer<E> consumer);

    <E extends Event & IGenericEvent<G> & IEventType<F>, G> void addGenericListener(int priority, Class<G> baseFilterClass, Class<E> eventClass, Consumer<E> consumer);

    void registerClass(Class<?> clazz);

    void registerObject(Object object);


    <E extends Event & IEventType<F>> void post(E event);

    void startup();
    void shutdown();

}
