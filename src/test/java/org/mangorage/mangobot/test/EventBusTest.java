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

package org.mangorage.mangobot.test;

import org.mangorage.eventbus.EventBus;
import org.mangorage.eventbus.annotations.SubscribeEvent;
import org.mangorage.eventbus.event.NormalEvent;
import org.mangorage.eventbus.interfaces.IEventBus;
import org.mangorage.eventbus.interfaces.IEventType;
import org.mangorage.mangobotapi.core.events.DiscordEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBusTest {

    public static class MyEvent extends NormalEvent {
    }

    public static class MyOther extends NormalEvent {
    }


    public static final ExecutorService ex = Executors.newWorkStealingPool();


    public static class Listeners {

        @SubscribeEvent
        public void onMyInstance(MyEvent event) {
            System.out.println("Instance!");
        }

        @SubscribeEvent
        public static void onMyStatic(MyEvent event) {
            System.out.println("Static!");
        }

        @SubscribeEvent(genericType = Integer.class)
        public static void onDiscord(DiscordEvent<Integer> event) {
            System.out.println(event.getInstance());
        }
    }


    public static void main(String[] args) throws InterruptedException {
        IEventBus<IEventType.INormalBusEvent> bus = EventBus.create();

        bus.registerClass(Listeners.class);
        bus.registerObject(new Listeners());

        bus.post(new MyEvent());
        bus.post(new DiscordEvent<>(100));
    }
}
