/*
 * Copyright (c) 2023. MangoRage
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
import org.mangorage.mangobotapi.core.modules.buttonactions.Actions;
import org.mangorage.mangobotapi.core.plugin.PluginLoader;
import org.mangorage.mboteventbus.EventBus;
import org.mangorage.mboteventbus.impl.IEventBus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoreMain {
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final IEventBus coreEventBus = EventBus.create();
    private static final AtomicBoolean running = new AtomicBoolean(false);
    public static IEventBus getCoreEventBus() {
        return coreEventBus;
    }

    static {
        coreEventBus.startup();
    }

    public static void main(String[] args) throws InterruptedException {
        running.set(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            System.out.println("Shutting down Mangoloader!");
        }));

        Actions.init();
        LanguageHandler.loadAll();
        PluginLoader.load();

        latch.await();

        coreEventBus.shutdown();
    }
}
