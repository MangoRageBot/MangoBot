package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.internal.command.CommandDispatcher;

public interface ICommandDispatcher<C, R> {
    static <C, R> ICommandDispatcher<C, R> create(R defaultResult) {
        return new CommandDispatcher<C, R>(defaultResult);
    }

    void register(AbstractCommand<C, R> command);
    AbstractCommand<C, R> getCommand(String name);

    R execute(String input, C context, CommandParseResult commandParseResult);
}
