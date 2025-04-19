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

package org.mangorage.commonutils.jda.slash.component.interact;


import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.mangorage.commonutils.jda.slash.command.watcher.EventWatcher;
import org.mangorage.commonutils.jda.slash.component.Component;
import org.mangorage.commonutils.jda.slash.component.NoRegistry;

import java.util.concurrent.TimeUnit;

public abstract class SmartDropdown<T extends SelectMenu> extends Component implements NoRegistry {

    protected SmartDropdown(String name) {
        super(name);
    }

    public abstract T build();

    protected abstract void onCreate();

    protected abstract void onRemove();

    @NotNull
    @Contract("_ -> new")
    public static StringSelect create(StringSelectMenu.Builder builder) {
        return new StringSelect(builder);
    }

    @NotNull
    @Contract("_ -> new")
    public static EntitySelect create(EntitySelectMenu.Builder builder) {
        return new EntitySelect(builder);
    }

    public static class StringSelect extends SmartDropdown<StringSelectMenu> implements NoRegistry {

        private final StringSelectMenu.Builder builder;
        private final EventWatcher<StringSelectInteractionEvent> eventWatcher;
        private StringSelectMenu parent;

        private StringSelect(StringSelectMenu.Builder builder) {
            super("SmartStringDropdown");
            this.builder = builder;

            eventWatcher = new EventWatcher<>(this, StringSelectInteractionEvent.class);
            super.create();
        }

        public StringSelect withListener(EventWatcher.Listener<StringSelectInteractionEvent> listener) {
            eventWatcher.setListener(listener);
            return this;
        }

        public StringSelect withListener(EventWatcher.Listener<StringSelectInteractionEvent> listener, int expireAfter, TimeUnit unit) {
            eventWatcher.setListener(listener, expireAfter, unit);
            return this;
        }

        protected void onCreate() {
            parent = builder.setId(getUuid().toString()).build();
        }

        protected void onRemove() {
            eventWatcher.destroy();
            parent = null;
        }

        public StringSelectMenu build() {
            return parent;
        }
    }

    public static class EntitySelect extends SmartDropdown<EntitySelectMenu> implements NoRegistry {

        private final EntitySelectMenu.Builder builder;
        private final EventWatcher<EntitySelectInteractionEvent> eventWatcher;
        private EntitySelectMenu parent;

        private EntitySelect(EntitySelectMenu.Builder builder) {
            super("SmartEntityDropdown");
            this.builder = builder;

            eventWatcher = new EventWatcher<>(this, EntitySelectInteractionEvent.class);
            super.create();
        }

        public EntitySelect withListener(EventWatcher.Listener<EntitySelectInteractionEvent> listener) {
            eventWatcher.setListener(listener);
            return this;
        }

        public EntitySelect withListener(EventWatcher.Listener<EntitySelectInteractionEvent> listener, int expireAfter, TimeUnit unit) {
            eventWatcher.setListener(listener, expireAfter, unit);
            return this;
        }

        protected void onCreate() {
            parent = builder.setId(getUuid().toString()).build();
        }

        protected void onRemove() {
            eventWatcher.destroy();
            parent = null;
        }

        public EntitySelectMenu build() {
            return parent;
        }
    }
}
