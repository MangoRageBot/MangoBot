/*
 * Copyright (c) 2023-2024. MangoRage
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
import org.mangorage.mangobot.loader.CoreMain;
import org.mangorage.mangobotapi.core.events.ShutdownEvent;
import org.mangorage.mangobotapi.core.events.StartupEvent;
import org.mangorage.mangobotapi.core.registry.commands.CommandRegistry;
import org.mangorage.mangobotapi.core.registry.permissions.PermissionRegistry;
import org.mangorage.mangobotapi.core.util.MessageSettings;

public abstract class CorePlugin extends AbstractPlugin {
    private final JDA JDA;
    private final String COMMAND_PREFIX = "!";
    private final MessageSettings DEFAULT_MESSAGE_SETTINGS = MessageSettings.create().build();
    private final CommandRegistry commandRegistry = new CommandRegistry(this);
    private final PermissionRegistry permissionRegistry = new PermissionRegistry(this);


    public CorePlugin(String id, JDA jda) {
        super(id);
        this.JDA = jda;

        getPluginBus().addListener(10, StartupEvent.class, event -> {
            switch (event.phase()) {
                case STARTUP -> {
                    startup();
                }
                case REGISTRATION -> {
                    registration();
                }
                case FINISHED -> {
                    finished();
                }
            }
        });

        getPluginBus().addListener(10, ShutdownEvent.class, event -> {
            switch (event.phase()) {
                case PRE -> {
                    shutdownPre();
                }
                case POST -> {
                    shutdownPost();
                }
            }
        });

        for (StartupEvent.Phase phase : StartupEvent.Phase.values())
            getPluginBus().post(new StartupEvent(phase));
    }

    public JDA getJDA() {
        return JDA;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public PermissionRegistry getPermissionRegistry() {
        return permissionRegistry;
    }

    public String getCommandPrefix() {
        return CoreMain.isDevMode() ? "dev" + COMMAND_PREFIX : COMMAND_PREFIX;
    }

    public MessageSettings getMessageSettings() {
        return DEFAULT_MESSAGE_SETTINGS;
    }

    public abstract void registration();

    public abstract void startup();

    public abstract void finished();

    public abstract void shutdownPre();

    public void shutdownPost() {
        for (ShutdownEvent.Phase phase : ShutdownEvent.Phase.values())
            getPluginBus().post(new ShutdownEvent(phase));
        getPluginBus().shutdown();
    }
}
