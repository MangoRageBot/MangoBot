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

package org.mangorage.commonutils.jda.slash.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;
import org.mangorage.commonutils.jda.slash.command.watcher.EventWatcher;


import java.util.ArrayList;
import java.util.List;

public class CommandOption {

    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autocomplete;
    private final List<Command.Choice> choices = new ArrayList<>();
    private EventWatcher.Listener<CommandAutoCompleteInteractionEvent> autoCompleteListener;

    public CommandOption(OptionType type, String name, String description, boolean required, boolean autocomplete) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autocomplete = autocomplete;

        if (autocomplete && !type.canSupportChoices())
            throw new IllegalStateException("This option type does not support auto completions");
    }

    public CommandOption(OptionType type, String name, String description, boolean required) {
        this(type, name, description, required, false);
    }

    public CommandOption(OptionType type, String name, String description) {
        this(type, name, description, false);
    }

    public CommandOption addChoice(Command.Choice choice) {
        choices.add(choice);
        return this;
    }

    public CommandOption addChoice(String name, String value) {
        choices.add(new Command.Choice(name, value));
        return this;
    }

    public CommandOption addChoices(Command.Choice... choices) {
        for (Command.Choice choice : choices)
            addChoice(choice);
        return this;
    }

    public CommandOption onAutoComplete(EventWatcher.Listener<CommandAutoCompleteInteractionEvent> listener) {
        if (!autocomplete)
            throw new IllegalStateException("Cannot add an autocomplete listener to a non-autocomplete option");
        autoCompleteListener = listener;
        return this;
    }

    public OptionType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public boolean required() {
        return required;
    }

    public boolean autocomplete() {
        return autocomplete;
    }

    public List<Command.Choice> choices() {
        return choices;
    }

    public @Nullable EventWatcher.Listener<CommandAutoCompleteInteractionEvent> autoCompleteListener() {
        return autoCompleteListener;
    }
}
