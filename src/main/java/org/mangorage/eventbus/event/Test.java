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

import org.mangorage.eventbus.EventBus;
import org.mangorage.eventbus.annotations.SubscribeEvent;
import org.mangorage.eventbus.interfaces.IEventBus;
import org.mangorage.eventbus.interfaces.IEventType;

public class Test {
    private static final IEventBus<IEventType.INormalBusEvent> NORMAL = EventBus.create(new NormalEventHandler(), IEventType.INormalBusEvent.class);


    public static class MyGeneric<S> extends NormalGenericEvent<S> {

        public MyGeneric(Class<S> tClass) {
            super(tClass);
        }
    }


    public static void main(String[] args) {
        NORMAL.registerClass(Test.class);
        NORMAL.post(new MyGeneric<>(String.class));
    }

    @SubscribeEvent(genericType = String.class)
    public static void test(MyGeneric<String> test) {
        System.out.println(test.getGenericType());
    }
}
