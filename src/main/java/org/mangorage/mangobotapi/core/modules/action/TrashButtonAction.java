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

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class TrashButtonAction extends ButtonAction {

    public TrashButtonAction() {
        super("trash", "trash");
    }

    public Button createForUser(User user) {
        return create(ButtonStyle.SECONDARY, user.getId());
    }

    @Override
    public boolean onClick(ButtonInteractionEvent event) {
        if (event.getComponent().getId().startsWith(getId() + ":")) {
            var list = event.getComponent().getId().split(":");
            var clicked = event.getUser().getId();
            var msg = event.getMessage();

            if (list[1].equals(clicked)) {
                msg.delete().queue();
                event.deferReply(true).setContent("Deleted Message!").queue();
            } else {
                event.deferReply(true).setContent("No permission to delete message!").queue();
            }
        }
        return false;
    }
}
