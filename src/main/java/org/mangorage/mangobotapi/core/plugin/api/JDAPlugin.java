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

package org.mangorage.mangobotapi.core.plugin.api;

import net.dv8tion.jda.api.JDA;
import org.mangorage.mangobotapi.core.events.ShutdownEvent;
import org.mangorage.mangobotapi.core.events.StartupEvent;
import org.mangorage.mangobotapi.core.registry.commands.CommandRegistry;
import org.mangorage.mangobotapi.core.registry.permissions.PermissionRegistry;
import org.mangorage.mangobotapi.core.util.MessageSettings;

/**
 * Holds a reference to a {@link JDA} instance
 * <p>
 * Useful if you have Multiple plugins with different bots and etc
 */
public abstract class JDAPlugin extends AbstractPlugin {
    private final JDA JDA;
    private final String COMMAND_PREFIX = "!";
    private final MessageSettings DEFAULT_MESSAGE_SETTINGS = MessageSettings.create().build();
    private final CommandRegistry commandRegistry = new CommandRegistry(this);
    private final PermissionRegistry permissionRegistry = new PermissionRegistry(this);


    public JDAPlugin(JDA jda) {
        this.JDA = jda;
    }

    // Call this to use default startup/shutdown behavior
    protected void init() {
        getPluginBus().addListener(10, StartupEvent.class, event -> {
            startup(event.phase());
        });

        getPluginBus().addListener(10, ShutdownEvent.class, event -> {
            shutdown(event.phase());
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
        return COMMAND_PREFIX;
    }

    public MessageSettings getMessageSettings() {
        return DEFAULT_MESSAGE_SETTINGS;
    }

    public abstract void startup(StartupEvent.Phase phase);

    public abstract void shutdown(ShutdownEvent.Phase phase);
}
