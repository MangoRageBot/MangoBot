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

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.mangorage.commonutils.jda.slash.command.watcher.EventWatcher;
import org.mangorage.commonutils.jda.slash.component.Component;
import org.mangorage.commonutils.jda.slash.component.NoRegistry;
import org.mangorage.commonutils.jda.slash.component.SendableComponent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartModal extends SendableComponent implements NoRegistry {

    private final Modal.Builder builder;
    private final EventWatcher<ModalInteractionEvent> eventWatcher;
    private Modal parent;

    protected SmartModal(Modal.Builder builder) {
        super("SmartModal");
        this.builder = builder;

        eventWatcher = new EventWatcher<>(this, ModalInteractionEvent.class, true);
    }

    public static SmartModal create(Modal.Builder builder) {
        SmartModal modal = new SmartModal(builder);
        modal.create();
        return modal;
    }

    public SmartModal withListener(EventWatcher.Listener<ModalInteractionEvent> listener) {
        eventWatcher.setListener(listener);
        return this;
    }

    public SmartModal withListener(EventWatcher.Listener<ModalInteractionEvent> listener, int expireAfter, TimeUnit unit) {
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

    protected MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        throw new UnsupportedOperationException("This component can only be used in slash commands, use reply instead");
    }

    protected ModalCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return event.replyModal(parent);
    }

    protected List<Component> getChildren() {
        return null;
    }

    public Modal build() {
        return parent;
    }
}
