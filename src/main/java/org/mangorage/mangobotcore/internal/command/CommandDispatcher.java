package org.mangorage.mangobotcore.internal.command;

import org.mangorage.mangobotcore.api.command.v1.ICommandContext;
import org.mangorage.mangobotcore.api.command.v1.ICommandDispatcher;
import org.mangorage.mangobotcore.api.command.v1.ICommandNode;
import org.mangorage.mangobotcore.api.command.v1.ICommandResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommandDispatcher implements ICommandDispatcher {
    private final Map<String, ICommandNode> roots = new HashMap<>();
    private final ICommandResult defaultInvalid;

    public CommandDispatcher(ICommandResult defaultInvalid) {
        this.defaultInvalid = defaultInvalid;
    }

    @Override
    public void register(ICommandNode commandNode) {
        roots.put(
                commandNode.getName(),
                commandNode
        );
    }

    @Override
    public ICommandNode getCommandNode(String name) {
        return roots.get(name);
    }

    @Override
    public ICommandResult execute(String input, ICommandContext context) {
        String[] split = input.trim().split("\\s+");
        if (split.length == 0)
            return defaultInvalid;

        ICommandNode root = roots.get(split[0]);
        if (root == null)
            return defaultInvalid;

        return root.execute(context, List.of(split).subList(1, split.length));
    }
}
