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

package org.mangorage.mangobotapi.core.util;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.mangorage.basicutils.misc.Lockable;
import org.mangorage.mangobotapi.core.modules.buttonactions.Actions;

public class MessageSettings {
    public static Builder create() {
        return new Builder();
    }

    public MessageSettings(Builder builder) {
    }

    public MessageCreateAction withDeletion(MessageCreateAction action, User user) {
        var button = Button.of(
                ButtonStyle.PRIMARY,
                Actions.TRASH.getId(user.getId()),
                Emoji.fromCustom("trash", 1150898672897359983L, false)
        );

        return action.addActionRow(button);
    }

    public MessageCreateAction apply(MessageCreateAction action) {
        return action.mentionRepliedUser(false).setSuppressedNotifications(true);
    }

    public static class Builder {
        private final Lockable lockable = new Lockable();

        private Builder self() {
            if (lockable.isLocked())
                throw new IllegalStateException("Unable to get self() as this builder has been built!");
            return this;
        }

        public MessageSettings build() {
            lockable.lock();
            return new MessageSettings(this);
        }
    }
}
