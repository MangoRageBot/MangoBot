/*
 * Copyright (c) 2023-2024. MangoRage
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

package org.mangorage.mangobotapi.core.plugin.api;


import org.mangorage.eventbus.EventBus;
import org.mangorage.eventbus.interfaces.IEventBus;
import org.mangorage.eventbus.interfaces.IEventType;

import java.nio.file.Path;
import java.util.function.Supplier;

import static org.mangorage.mangobotapi.core.plugin.api.InterPluginMessage.send;

public abstract class AbstractPlugin {
    private final IEventBus<IEventType.INormalBusEvent> pluginBus = EventBus.create();
    private final String id;

    public AbstractPlugin(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Path getPluginDirectory() {
        return Path.of("plugins/%s/".formatted(getId())).toAbsolutePath();
    }

    protected void sendInterPluginMessage(String sendTo, String method, Supplier<?> objectSupplier) {
        send(this, sendTo, method, objectSupplier);
    }

    public IEventBus<IEventType.INormalBusEvent> getPluginBus() {
        return pluginBus;
    }
}
