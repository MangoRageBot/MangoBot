/*
 * Copyright (c) 2023-2025. MangoRage
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

package org.mangorage.mangobotapi.core.registry.commands;

import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.events.BasicCommandEvent;
import org.mangorage.mangobotapi.core.plugin.api.JDAPlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public final class CommandRegistry {

    private final JDAPlugin plugin;

    public CommandRegistry(JDAPlugin plugin) {
        this.plugin = plugin;
    }


    private final CopyOnWriteArrayList<IBasicCommand> COMMANDS = new CopyOnWriteArrayList<>();

    public void addBasicCommand(IBasicCommand command) {
        plugin.getPluginBus().addListener(10, BasicCommandEvent.class, command.getListener());
        COMMANDS.add(command);
    }

    public void postBasicCommand(BasicCommandEvent event) {
        plugin.getPluginBus().post(event);
    }

    public IBasicCommand getCommand(String commandId) {
        for (IBasicCommand command : COMMANDS) {
            if (command.isValidCommand(commandId)) {
                return command;
            }
        }
        return null;
    }

    public String getUsage(String commandId) {
        var cmd = getCommand(commandId);
        return cmd != null ? cmd.usage() : null;
    }

    public List<String> getAliases(String commandId) {
        var cmd = getCommand(commandId);
        return cmd != null ? cmd.commandAliases() : List.of();
    }
}
