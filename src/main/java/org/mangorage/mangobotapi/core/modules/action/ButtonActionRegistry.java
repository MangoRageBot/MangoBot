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

package org.mangorage.mangobotapi.core.modules.action;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonActionRegistry {
    private final List<ButtonAction> ACTIONS = new ArrayList<>();
    private final Map<Class<?>, ButtonAction> ACTIONS_BY_MAP = new HashMap<>();

    public void register(ButtonAction buttonAction) {
        this.ACTIONS.add(buttonAction);
        this.ACTIONS_BY_MAP.put(buttonAction.getClass(), buttonAction);
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
