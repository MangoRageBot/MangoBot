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

package org.mangorage.mangobotapi.core.plugin;

import org.mangorage.mangobotapi.core.plugin.api.AbstractPlugin;

public final class PluginContainer {
    private final String id;
    private final Class<?> entrypoint;
    private final PluginMetadata metadata;

    AbstractPlugin instance = null;

    public PluginContainer(String id, Class<?> entrypoint, PluginMetadata metadata) {
        this.id = id;
        this.entrypoint = entrypoint;
        this.metadata = metadata;
    }

    // TODO: Have better handling of creating new instance
    void initInstance() throws InstantiationException, IllegalAccessException {
        instance = (AbstractPlugin) entrypoint.newInstance();
    }

    public String getId() {
        return id;
    }

    public AbstractPlugin getInstance() {
        return instance;
    }

    public String getType() {
        return metadata.type();
    }

    public PluginMetadata getMetadata() {
        return metadata;
    }
}