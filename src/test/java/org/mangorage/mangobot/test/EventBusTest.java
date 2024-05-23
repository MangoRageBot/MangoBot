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
import org.mangorage.eventbus.event.Event;
import org.mangorage.mangobot.test.misc.TimedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBusTest {

    public static class MyEvent extends Event {
    }

    public static class MyOther extends MyEvent {
    }


    public static final ExecutorService ex = Executors.newWorkStealingPool();


    public static void main(String[] args) throws InterruptedException {
        var bus = EventBus.create();

        var registration = TimedTest.of(() -> {
            for (int i = 0; i < 1; i++) {
                bus.addListener(10, MyOther.class, e -> {
                    System.out.println("LOL");
                });
            }
        });

        long amount = 10;
        var post = TimedTest.of(() -> {
            for (int i = 0; i < amount; i++) {
                bus.post(new MyOther());
                bus.post(new Event());
            }
        });

        List<MyEvent> myEvents = new ArrayList<>();
        for (int i = 0; i < amount; i++)
            myEvents.add(new MyOther());

        System.out.println("Registration time for 1 listener -> " + registration.getResult());
        System.out.println("AVG -> " + post.getResult() / amount);
        var a = 1;
    }
}
