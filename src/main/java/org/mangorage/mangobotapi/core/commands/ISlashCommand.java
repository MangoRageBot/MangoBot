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

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.mangorage.mangobotapi.core.events.SlashCommandEvent;
import org.mangorage.mboteventbus.impl.IEvent;

public interface ISlashCommand extends ICommand<SlashCommandInteraction, SlashCommandEvent> {
    default IEvent<SlashCommandEvent> getListener() {
        return (e) -> {
            if (isValidCommand(e.getCommand())) {
                var interaction = e.getInteraction();
                var guild = interaction.isFromGuild();


                if (!commandType().isAllowed(interaction)) return;

                if (guild)
                    if (!allowedGuilds().isEmpty() && !allowedGuilds().contains(interaction.getGuild().getId())) return;
                if (!allowedUsers().isEmpty() && !allowedUsers().contains(interaction.getUser().getId())) return;

                try {
                    e.setHandled(execute(interaction, e.getArguments()));
                } catch (Exception ex) {
                    e.setHandled(CommandResult.PASS);
                    e.setException(ex);
                }
            }
        };
    }

    String description();

    default void registerSubCommands(SlashCommandData command) {
    }

    default void onAutoCompleteEvent(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals(commandId())) onAutoCompleteEvent(event.getInteraction());
    }

    void onAutoCompleteEvent(CommandAutoCompleteInteraction interaction);
}
