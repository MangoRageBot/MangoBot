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

package org.mangorage.mangobotcore.api.util.jda.slash.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangobotcore.api.util.jda.slash.command.Unique;

import java.util.UUID;

public abstract class Component {

    protected UUID uuid;
    protected final String name;
    private @Nullable Object identifier;

    protected Component(String name) {
        this.name = name;
    }

    protected abstract void onCreate();

    protected abstract void onRemove();

    public @NotNull Component create() {
        if (isCreated())
            return this;

        uuid = UUID.randomUUID();
        onCreate();
        return this;
    }

    public void remove() {
        if (!isCreated())
            return;

        onRemove();
        uuid = null;
    }

    public final boolean isCreated() {
        return uuid != null;
    }

    /**
     * Useful for identifying the component between multiple components of the same type
     * <p><b>NOTE: </b> The identifier must be unique</p>
     *
     * @param identifier The identifier, can be anything
     */
    public void setIdentifier(@Nullable Object identifier) {
        Unique.checkUnique("component", identifier, "Component identifier must be unique");
        this.identifier = identifier;
    }

    public @Nullable Object getIdentifier() {
        return identifier;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final String getName() {
        return name;
    }
}
