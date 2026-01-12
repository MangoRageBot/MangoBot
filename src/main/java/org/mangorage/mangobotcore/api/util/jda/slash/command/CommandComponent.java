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

package org.mangorage.mangobotcore.api.util.jda.slash.command;



import org.mangorage.mangobotcore.api.util.jda.slash.component.Component;
import org.mangorage.mangobotcore.api.util.jda.slash.component.NoRegistry;
import org.mangorage.mangobotcore.api.util.jda.slash.command.watcher.EventWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Internal use only</b><br>
 * Used to register slash commands with an {@link EventWatcher}
 */

public final class CommandComponent extends Component implements NoRegistry {

    private boolean contextCommand;
    private List<String> aliases;

    CommandComponent(String name) {
        super(name);
    }

    CommandComponent(String name, List<String> aliases) {
        this(name);
        this.aliases = new ArrayList<>(aliases);
    }

    CommandComponent(String name, List<String> aliases, String suffix) {
        this(name + suffix, aliases);
        this.aliases.replaceAll(s -> s + suffix);
    }

    public CommandComponent setContextCommand(boolean contextCommand) {
        this.contextCommand = contextCommand;
        return this;
    }

    public boolean isContextCommand() {
        return contextCommand;
    }

    public List<String> getAliases() {
        return aliases;
    }

    protected void onCreate() {
    }

    protected void onRemove() {
    }
}
