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
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.mangorage.commonutils.jda.slash.command.watcher.EventWatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Command {

    public static final List<CommandData> globalCommands = new ArrayList<>();


    public static SlashCommandBuilder slash(@NotNull String name, @NotNull String description) {
        Checks.notEmpty(name, "Name");
        Checks.notEmpty(description, "Description");
        Unique.checkUnique("slashcommand", name, "A slash command with the name '" + name + "' already exists");
        return new SlashCommandBuilder(name, description);
    }

    public static MessageContextBuilder message(@NotNull String name) {
        Checks.notEmpty(name, "Name");
        Unique.checkUnique("message context", name, "A message context command with the name '" + name + "' already exists");
        return new MessageContextBuilder(name);
    }


    public static UserContextBuilder user(@NotNull String name) {
        Checks.notEmpty(name, "Name");
        Unique.checkUnique("message context", name, "A user context command with the name '" + name + "' already exists");
        return new UserContextBuilder(name);
    }

    interface Buildable<R, T extends CommandData> extends GlobalModifiers<R, T> {
        /**
         * Builds and creates an event watcher for the command, and returns the command data.<br>
         * <b>You will still need to register the command with JDA</b>
         *
         * @return the command data
         */
        T build();

        /**
         * Builds the command and automatically registers it with JDA.<br>
         * <b>Make sure to call this before actually starting a JDA instance, as commands will be registered in the {@link ReadyEvent}</b>
         */
        default void buildAndRegister() {
            T data = build();
            globalCommands.add(data);
        }
    }

    @SuppressWarnings("unchecked")
    interface GlobalModifiers<R, T extends CommandData> {
        T getData();

        default R modifyData(@NotNull Function<T, T> function) {
            function.apply(getData());
            return (R) this;
        }

        default R setDefaultPermissions(DefaultMemberPermissions defaultPermissions) {
            getData().setDefaultPermissions(defaultPermissions);
            return (R) this;
        }

        default R setNSFW(boolean nsfw) {
            getData().setNSFW(nsfw);
            return (R) this;
        }

        default R setGuildOnly() {
            getData().setGuildOnly(true);
            return (R) this;
        }
    }

    public static class SlashCommandBuilder implements Buildable<SlashCommandBuilder, SlashCommandData> {

        private final SlashCommandData data;
        private final List<String> aliases = new ArrayList<>();

        private final Map<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> autocompleteListeners = new HashMap<>();
        private final List<Function<SlashCommandInteractionEvent, Boolean>> conditions = new ArrayList<>();
        private EventWatcher.Listener<SlashCommandInteractionEvent> listener;

        //Sub commands
        private final Map<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> subCommandAutoCompleteListeners = new HashMap<>();
        private final Map<String, List<Function<SlashCommandInteractionEvent, Boolean>>> subCommandConditions = new HashMap<>();
        private final Map<String, EventWatcher.Listener<SlashCommandInteractionEvent>> subCommandListeners = new HashMap<>();
        private boolean containsSubCommands;

        private SlashCommandBuilder(String name, String description) {
            data = Commands.slash(name, description);
        }

        public SlashCommandBuilder addOption(@NotNull CommandOption option) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add an option to a slash command that contains sub commands");

            OptionData optionData = new OptionData(option.type(), option.name(), option.description(), option.required(), option.autocomplete());
            for (net.dv8tion.jda.api.interactions.commands.Command.Choice choice : option.choices())
                optionData.addChoice(choice.getName(), choice.getAsString());
            getData().addOptions(optionData);

            if (option.autocomplete() && option.autoCompleteListener() != null)
                autocompleteListeners.put(option.name(), option.autoCompleteListener());

            return this;
        }

        public SlashCommandBuilder addOptions(@NotNull CommandOption... options) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add an option to a slash command that contains sub commands");

            for (CommandOption option : options)
                addOption(option);
            return this;
        }

        public SlashCommandBuilder executes(EventWatcher.Listener<SlashCommandInteractionEvent> listener) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add a listener to a slash command that contains sub commands");
            this.listener = listener;
            return this;
        }

        public SlashCommandBuilder addCondition(Function<SlashCommandInteractionEvent, Boolean> condition) {
            if (containsSubCommands)
                throw new IllegalStateException("Cannot add a condition to a slash command that contains sub commands");
            this.conditions.add(condition);
            return this;
        }

        /**
         * Adds an alias to the slash command<br>
         * <b>Note - only works when calling {@link #buildAndRegister()}</b>
         *
         * @param alias the alias to add
         * @return the builder
         */
        public SlashCommandBuilder addAlias(@NotNull String alias) {
            if (alias.equals(getData().getName()))
                throw new IllegalArgumentException("Alias cannot be the same as the command name");
            if (aliases.contains(alias))
                throw new IllegalArgumentException("Alias '" + alias + "' already exists");
            Unique.checkUnique("slashcommand", alias, "A slash command or alias with the name '" + alias + "' already exists");

            aliases.add(alias);
            return this;
        }

        /**
         * Adds multiple aliases to the slash command<br>
         * <b>Note - only works when calling {@link #buildAndRegister()}</b>
         *
         * @param aliases the aliases to add
         * @return the builder
         */
        public SlashCommandBuilder addAliases(@NotNull String... aliases) {
            Arrays.stream(aliases).forEach(this::addAlias);
            return this;
        }

        public SubCommandBuilder addSubCommand(String name, String description) {
            if (listener != null)
                throw new IllegalStateException("Cannot add a sub command to a slash command that contains a listener");
            containsSubCommands = true;
            return new SubCommandBuilder(this, name, description);
        }

        public SlashCommandData getData() {
            return data;
        }

        public SlashCommandData build() {
            if (!containsSubCommands && listener != null) {
                new EventWatcher<>(new CommandComponent(getData().getName(), aliases), SlashCommandInteractionEvent.class)
                        .setListener(listener)
                        .addConditions(conditions);

                for (Map.Entry<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> entry : autocompleteListeners.entrySet())
                    new EventWatcher<>(new OptionComponent(getData().getName() + " " + entry.getKey()), CommandAutoCompleteInteractionEvent.class)
                            .setListener(entry.getValue());
            } else {
                for (Map.Entry<String, EventWatcher.Listener<SlashCommandInteractionEvent>> entry : subCommandListeners.entrySet())
                    new EventWatcher<>(new CommandComponent(getData().getName(), aliases, " " + entry.getKey()), SlashCommandInteractionEvent.class)
                            .setListener(entry.getValue())
                            .addConditions(subCommandConditions.getOrDefault(entry.getKey(), Collections.emptyList()));

                for (Map.Entry<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> entry : subCommandAutoCompleteListeners.entrySet())
                    new EventWatcher<>(new OptionComponent(getData().getName() + " " + entry.getKey()), CommandAutoCompleteInteractionEvent.class)
                            .setListener(entry.getValue());
            }
            return getData();
        }

        public void buildAndRegister() {
            Buildable.super.buildAndRegister();
            aliases.forEach(alias -> globalCommands.add(SlashCommandData.fromData(getData().toData()).setName(alias)));
        }

        public static class SubCommandBuilder {

            private final SlashCommandBuilder parent;
            private final SubcommandData data;
            private final Map<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> autocompleteListeners = new HashMap<>();
            private final List<Function<SlashCommandInteractionEvent, Boolean>> conditions = new ArrayList<>();
            private EventWatcher.Listener<SlashCommandInteractionEvent> listener;

            private SubCommandBuilder(SlashCommandBuilder parent, String name, String description) {
                this.parent = parent;
                data = new SubcommandData(name, description);
            }

            public SubCommandBuilder addOption(@NotNull CommandOption option) {
                data.addOption(option.type(), option.name(), option.description(), option.required(), option.autocomplete());

                if (option.autocomplete() && option.autoCompleteListener() != null)
                    autocompleteListeners.put(option.name(), option.autoCompleteListener());

                return this;
            }

            public SubCommandBuilder addOptions(@NotNull CommandOption... options) {
                for (CommandOption option : options)
                    addOption(option);
                return this;
            }

            public SubCommandBuilder executes(EventWatcher.Listener<SlashCommandInteractionEvent> listener) {
                if (this.listener != null)
                    throw new IllegalStateException("Cannot add a listener to a sub command that already has a listener");
                this.listener = listener;
                return this;
            }

            public SubCommandBuilder addCondition(Function<SlashCommandInteractionEvent, Boolean> condition) {
                this.conditions.add(condition);
                return this;
            }

            public SubCommandBuilder modifyData(@NotNull Function<SubcommandData, SubcommandData> function) {
                function.apply(data);
                return this;
            }

            public SlashCommandBuilder build() {
                parent.getData().addSubcommands(data);
                if (listener != null) {
                    parent.subCommandListeners.put(data.getName(), listener);

                    if (!conditions.isEmpty())
                        parent.subCommandConditions.put(data.getName(), conditions);

                    for (Map.Entry<String, EventWatcher.Listener<CommandAutoCompleteInteractionEvent>> entry : autocompleteListeners.entrySet())
                        parent.subCommandAutoCompleteListeners.put(data.getName() + " " + entry.getKey(), entry.getValue());
                }
                return parent;
            }
        }
    }

    public static class MessageContextBuilder implements Buildable<MessageContextBuilder, CommandData> {

        private final CommandData data;
        private final List<Function<MessageContextInteractionEvent, Boolean>> conditions = new ArrayList<>();
        private EventWatcher.Listener<MessageContextInteractionEvent> listener;

        private MessageContextBuilder(String name) {
            this.data = Commands.message(name);
        }

        public MessageContextBuilder executes(EventWatcher.Listener<MessageContextInteractionEvent> listener) {
            this.listener = listener;
            return this;
        }

        public MessageContextBuilder addCondition(Function<MessageContextInteractionEvent, Boolean> condition) {
            this.conditions.add(condition);
            return this;
        }

        public CommandData getData() {
            return data;
        }

        public CommandData build() {
            if (listener != null) {
                new EventWatcher<>(new CommandComponent(getData().getName()).setContextCommand(true), MessageContextInteractionEvent.class)
                        .setListener(listener)
                        .addConditions(conditions);
            }
            return getData();
        }
    }

    public static class UserContextBuilder implements Buildable<UserContextBuilder, CommandData> {

        private final CommandData data;
        private final List<Function<UserContextInteractionEvent, Boolean>> conditions = new ArrayList<>();
        private EventWatcher.Listener<UserContextInteractionEvent> listener;

        private UserContextBuilder(String name) {
            this.data = Commands.user(name);
        }

        public UserContextBuilder executes(EventWatcher.Listener<UserContextInteractionEvent> listener) {
            this.listener = listener;
            return this;
        }

        public UserContextBuilder addCondition(Function<UserContextInteractionEvent, Boolean> condition) {
            this.conditions.add(condition);
            return this;
        }

        public CommandData getData() {
            return data;
        }

        public CommandData build() {
            if (listener != null) {
                new EventWatcher<>(new CommandComponent(getData().getName()).setContextCommand(true), UserContextInteractionEvent.class)
                        .setListener(listener)
                        .addConditions(conditions);
            }
            return getData();
        }
    }
}
