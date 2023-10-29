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

package org.mangorage.mangobotapi.core.plugin.api;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.mangorage.mangobotapi.core.events.LoadEvent;
import org.mangorage.mangobotapi.core.events.SaveEvent;
import org.mangorage.mangobotapi.core.events.ShutdownEvent;
import org.mangorage.mangobotapi.core.events.StartupEvent;
import org.mangorage.mangobotapi.core.modules.buttonactions.Actions;
import org.mangorage.mangobotapi.core.registry.commands.CommandRegistry;
import org.mangorage.mangobotapi.core.util.MessageSettings;
import org.mangorage.mboteventbus.impl.IEventBus;

public class CorePlugin extends Plugin {
    private final JDA JDA;
    private final IEventBus pluginBus;
    private final String COMMAND_PREFIX = "!";
    private final MessageSettings DEFAULT_MESSAGE_SETTINGS = MessageSettings.create().build();
    private final CommandRegistry commandRegistry = new CommandRegistry(this);

    public CorePlugin(JDABuilder builder, IEventBus pluginBus) {
        this.JDA = builder.build();
        this.pluginBus = pluginBus;
        startup();
    }

    public JDA getJDA() {
        return JDA;
    }

    public IEventBus getPluginBus() {
        return pluginBus;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public String getCommandPrefix() {
        return COMMAND_PREFIX;
    }

    public MessageSettings getMessageSettings() {
        return DEFAULT_MESSAGE_SETTINGS;
    }

    public void loadCommands() {
    }

    public void startup() {
        getPluginBus().startup();

        getPluginBus().addListener(10, StartupEvent.class, event -> {
            switch (event.phase()) {
                case STARTUP -> {
                    loadCommands();

                    Actions.init();
                }
                case REGISTRATION -> {
                }
                case FINISHED -> {
                    getPluginBus().post(new LoadEvent());
                }
            }
        });

        getPluginBus().addListener(10, ShutdownEvent.class, event -> {
            switch (event.phase()) {
                case PRE -> {
                }
                case POST -> {
                    getPluginBus().post(new SaveEvent());
                }
            }
        });

        for (StartupEvent.Phase phase : StartupEvent.Phase.values())
            getPluginBus().post(new StartupEvent(phase));
    }

    public void shutdown() {
        for (ShutdownEvent.Phase phase : ShutdownEvent.Phase.values())
            getPluginBus().post(new ShutdownEvent(phase));
        getPluginBus().shutdown();
    }
}
