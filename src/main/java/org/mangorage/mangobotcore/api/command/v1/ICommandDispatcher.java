package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.internal.command.CommandDispatcher;

public interface ICommandDispatcher {
    static ICommandDispatcher create(ICommandResult invalid) {
        return new CommandDispatcher(invalid);
    }

    void register(ICommandNode commandNode);
    ICommandNode getCommandNode(String name);

    ICommandResult execute(String input, ICommandContext context);
}
