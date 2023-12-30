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

package org.mangorage.mangobotapi.core.commands;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.function.Predicate;

public enum CommandType {
    BOTH(a -> true),
    DM_ONLY(a -> !a),
    GUILD_ONLY(a -> a);

    private final Predicate<Boolean> predicate;

    CommandType(Predicate<Boolean> predicate) {
        this.predicate = predicate;
    }

    public boolean isAllowed(Message message) {
        return predicate.test(message.isFromGuild());
    }

    public boolean isAllowed(Interaction interaction) {
        return predicate.test(interaction.isFromGuild());
    }
}
