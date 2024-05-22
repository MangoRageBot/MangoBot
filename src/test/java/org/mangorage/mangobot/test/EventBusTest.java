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

import org.mangorage.mangobot.test.misc.TimedTest;
import org.mangorage.mboteventbus.base.Event;
import org.mangorage.mboteventbus.base.EventBus;

public class EventBusTest {

    public static class MyEvent extends Event {
    }


    public static void main(String[] args) {
        var bus = EventBus.create();

        var registration = TimedTest.of(() -> {
            for (int i = 0; i < 10000; i++) {
                bus.addListener(10, Event.class, e -> {
                });
                bus.addListener(10, MyEvent.class, e -> {
                });
            }
        });

        var post = TimedTest.of(() -> {
            bus.post(new MyEvent());
        });

        System.out.println("Registration time for 1 listener -> " + registration.getResult());
        long total = 0;
        long amount = 10000;

        for (int i = 0; i < amount; i++) {
            var result = post.getResult();
            System.out.println("Post time for Post #%s -> ".formatted(i) + result);
            total += result;
        }
        System.out.println("AVG -> " + total / amount);
    }
}
