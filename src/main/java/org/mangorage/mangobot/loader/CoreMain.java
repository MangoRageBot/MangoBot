/*
 * Copyright (c) 2023-2025. MangoRage
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

package org.mangorage.mangobot.loader;

import org.mangorage.basicutils.language.LanguageHandler;
import org.mangorage.eventbus.EventBus;
import org.mangorage.eventbus.event.NormalEventHandler;
import org.mangorage.eventbus.interfaces.IEventBus;
import org.mangorage.eventbus.interfaces.IEventType;
import org.mangorage.mangobot.misc.Example;
import org.mangorage.mangobotapi.core.plugin.PluginLoader;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CoreMain {
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final IEventBus<IEventType.INormalBusEvent> coreEventBus = EventBus.create(new NormalEventHandler(), IEventType.INormalBusEvent.class);
    private static final AtomicBoolean running = new AtomicBoolean(false);

    public static IEventBus<IEventType.INormalBusEvent> getCoreEventBus() {
        return coreEventBus;
    }

    private static List<String> args;
    private static boolean isDev = false;

    public static boolean isDevMode() {
        return isDev;
    }

    public static List<String> getArgs() {
        return args;
    }

    public static void main(String[] args) throws InterruptedException {
        running.set(true);
        System.out.println("Supplied Args -> " + Arrays.toString(args));
        CoreMain.args = List.of(args);

        for (String arg : args) {
            if (arg.contains("--dev")) {
                isDev = true;
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            System.out.println("Shutting down Mangoloader!");
        }));

        LanguageHandler.loadAll();
        PluginLoader.load();

        System.out.println("Testing Class Transformers");
        Example<Integer> a = new Example<>() {
        };
        System.out.println(a.getTypeToken().getType());

        latch.await();
        coreEventBus.shutdown();
    }
}
