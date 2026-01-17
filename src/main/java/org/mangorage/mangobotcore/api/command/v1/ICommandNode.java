package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.internal.command.CommandNodeBuilder;

import java.util.List;

public interface ICommandNode {
    static ICommandNodeBuilder create(String name) {
        return new CommandNodeBuilder(name);
    }

    String getName();
    String getUsage();
    List<ICommandNode> getSubNodes();

    ICommandResult execute(ICommandContext commandContext, List<String> arguments);
}
