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

package org.mangorage.commonutils.jda;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ButtonActionRegistry {
    private final Set<ButtonAction> ACTIONS = new HashSet<>();
    private final Map<Class<?>, ButtonAction> ACTIONS_BY_MAP = new HashMap<>();
    private final Set<String> ACTIONS_REGISTERED = new HashSet<>();

    public void register(ButtonAction buttonAction) {
        if (ACTIONS.contains(buttonAction) || ACTIONS_BY_MAP.containsKey(buttonAction.getClass()))
            throw new IllegalStateException("Cannot register Button Action %s due to already being registered".formatted(buttonAction.getId()));
        if (ACTIONS_REGISTERED.contains(buttonAction.getId()))
            throw new IllegalStateException("Cannot register Button Action %s due to a Button Action with Similar Id being present".formatted(buttonAction.getId()));

        this.ACTIONS.add(buttonAction);
        this.ACTIONS_BY_MAP.put(buttonAction.getClass(), buttonAction);
        this.ACTIONS_REGISTERED.add(buttonAction.getId());
    }

    public void post(ButtonInteractionEvent event) {
        for (ButtonAction action : ACTIONS) {
            if (action.onClick(event)) break;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ButtonAction> T get(Class<T> tClass) {
        var result = ACTIONS_BY_MAP.get(tClass);
        return result == null ? null : (T) result;
    }
}
