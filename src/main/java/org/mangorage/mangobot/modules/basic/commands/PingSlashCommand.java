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

package org.mangorage.mangobot.modules.basic.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.ISlashCommand;

import java.time.temporal.ChronoUnit;

public class PingSlashCommand implements ISlashCommand {

    @Override
    public CommandResult execute(SlashCommandInteraction interaction, Arguments args) {

        interaction.reply("Ping: ...").queue(m -> {
            m.retrieveOriginal().queue(original -> {
                long ping = interaction.getTimeCreated().until(original.getTimeCreated(), ChronoUnit.MILLIS);
                m.editOriginal("Ping: " + ping + "ms | Websocket: " + interaction.getJDA().getGatewayPing() + "ms").queue();
            });
        });

        return CommandResult.PASS;
    }


    @Override
    public String commandId() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Pings the bot!";
    }
}