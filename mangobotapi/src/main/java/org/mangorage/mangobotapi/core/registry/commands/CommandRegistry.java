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

package org.mangorage.mangobotapi.core.registry.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.commands.ICommand;
import org.mangorage.mangobotapi.core.commands.ISlashCommand;
import org.mangorage.mangobotapi.core.events.BasicCommandEvent;
import org.mangorage.mangobotapi.core.events.SlashCommandEvent;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;
import org.mangorage.mboteventbus.base.EventHolder;
import org.mangorage.mboteventbus.impl.IEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@SuppressWarnings({"rawtypes", "unchecked"})
public class CommandRegistry {

    private final CorePlugin plugin;

    public CommandRegistry(CorePlugin plugin) {
        this.plugin = plugin;
    }

    private final EventHolder<BasicCommandEvent> BASIC_COMMAND_EVENT = EventHolder.create(
            BasicCommandEvent.class,
            (i) -> (e) -> {
                for (IEventListener<BasicCommandEvent> listener : i) {
                    listener.invoke(e);
                }
            });

    public final EventHolder<SlashCommandEvent> SLASH_COMMAND_EVENT = EventHolder.create(
            SlashCommandEvent.class,
            (i) -> (e) -> {
                for (IEventListener<SlashCommandEvent> listener : i) {
                    listener.invoke(e);
                }
            });

    private final CopyOnWriteArrayList<ICommand<?, ?>> COMMANDS = new CopyOnWriteArrayList<>();

    public static void load() {
        /**
        ReflectionsUtils.REFLECTIONS.getTypesAnnotatedWith(AutoRegister.BasicCommand.class).forEach(cls -> {
            var obj = ReflectionsUtils.createInstance(cls);
            if (obj == null) throw new IllegalStateException("Unable to auto register command");

            if (obj instanceof IBasicCommand command) {
                addBasicCommand(command);
            } else
                throw new IllegalStateException("Unable to auto register command. Class must implement IBasicCommand");
        });

        ReflectionsUtils.REFLECTIONS.getTypesAnnotatedWith(AutoRegister.SlashCommand.class).forEach(cls -> {
            var obj = ReflectionsUtils.createInstance(cls);
            if (obj == null) throw new IllegalStateException("Unable to auto register command");

            if (obj instanceof ISlashCommand command) {
                addSlashCommand(command);
            } else
                throw new IllegalStateException("Unable to auto register command. Class must implement ISlashCommand");
        });
         **/

        // TODO: FIX THIS
    }

    public void addBasicCommand(IBasicCommand command) {
        BASIC_COMMAND_EVENT.addListener(command.getListener());
        COMMANDS.add(command);
    }

    public void addSlashCommand(ISlashCommand command) {
        var updateAction = plugin.getJDA().updateCommands();
        var commandData = Commands.slash(command.commandId(), command.description());
        command.registerSubCommands(commandData);
        updateAction.addCommands(commandData).queue();
        SLASH_COMMAND_EVENT.addListener(command.getListener());

        // COMMANDS.add(command); // Implement this later..., for now just add the basic commands...
    }

    public void postBasicCommand(BasicCommandEvent event) {
        BASIC_COMMAND_EVENT.post(event);
    }

    public void postSlashCommand(SlashCommandEvent event) {
        SLASH_COMMAND_EVENT.post(event);
    }

    public ICommand getCommand(String commandId) {
        for (ICommand<?, ?> command : COMMANDS) {
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
