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

package org.mangorage.jdautils;


import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.mangorage.jdautils.component.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class EventWatcher<E extends Event> {

    private final Component component;
    private final Class<E> eventClass;
    private final List<Function<E, Boolean>> conditions = new ArrayList<>();
    private Listener<E> listener;
    private long expireTime = -1;
    private boolean singleUse = false;

    public EventWatcher(Component component, Class<E> eventClass) {
        this.component = component;
        this.eventClass = eventClass;
    }

    public EventWatcher(Component component, Class<E> eventClass, boolean singleUse) {
        this(component, eventClass);
        this.singleUse = singleUse;
    }

    public EventWatcher<E> setListener(Listener<E> listener) {
        this.listener = listener;
        WatcherManager.addWatcher(this);
        return this;
    }

    public EventWatcher<E> setListener(Listener<E> listener, int expireAfter, @NotNull TimeUnit unit) {
        this.expireTime = System.currentTimeMillis() + unit.toMillis(expireAfter);
        this.setListener(listener);
        return this;
    }

    public EventWatcher<E> addCondition(@NotNull Function<E, Boolean> condition) {
        Checks.notNull(condition, "Condition");
        Checks.notNull(eventClass, "Event class");
        conditions.add(condition);
        return this;
    }

    public EventWatcher<E> addConditions(@NotNull List<Function<E, Boolean>> conditions) {
        Checks.notNull(conditions, "Conditions");
        conditions.forEach(this::addCondition);
        return this;
    }

    public Listener<E> getListener() {
        return listener;
    }

    public void destroy() {
        listener = null;
        expireTime = -1;
        WatcherManager.removeWatcher(this);
    }

    void onEvent(Event rawEvent) {
        if (expired()) {
            destroy();
            return;
        }

        if (listener != null && rawEvent.getClass().equals(eventClass)) {
            E event = (E) rawEvent;

            boolean conditionsMet = conditions.stream().allMatch(condition -> condition.apply(event));
            if (!conditionsMet) return;

            listener.onEvent(event);
            if (singleUse)
                destroy();
        }
    }

    public Component getComponent() {
        return component;
    }

    public boolean expired() {
        return expireTime != -1 && System.currentTimeMillis() > expireTime;
    }

    public interface Listener<E> {
        void onEvent(E event);
    }
}
