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

package org.mangorage.eventbus.event;

import org.mangorage.eventbus.ListenerType;
import org.mangorage.eventbus.event.core.GenericEvent;
import org.mangorage.eventbus.interfaces.IEventType;
import org.mangorage.eventbus.interfaces.IEventTypeHandler;

public class NormalEventHandler implements IEventTypeHandler<NormalEvent, NormalGenericEvent<?>, IEventType.INormalBusEvent> {


    @Override
    public boolean canHandle(Class<?> eventClass, ListenerType listenerType) {
        return switch (listenerType) {
            case NORMAL -> NormalEvent.class.isAssignableFrom(eventClass);
            case GENERIC -> NormalGenericEvent.class.isAssignableFrom(eventClass);
        };
    }

    @Override
    public <GT, T extends GenericEvent<GT> & IEventType<IEventType.INormalBusEvent>> Class<T> castGenericClass(Class<?> clz, Class<GT> genericType) {
        return (Class<T>) clz;
    }

}
