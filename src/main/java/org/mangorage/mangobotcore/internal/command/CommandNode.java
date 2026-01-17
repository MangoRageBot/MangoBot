package org.mangorage.mangobotcore.internal.command;

import org.mangorage.mangobotcore.api.command.v1.ICommandContext;
import org.mangorage.mangobotcore.api.command.v1.ICommandExecutor;
import org.mangorage.mangobotcore.api.command.v1.ICommandNode;
import org.mangorage.mangobotcore.api.command.v1.ICommandResult;
import org.mangorage.mangobotcore.api.jda.command.v1.CommandResult;
import org.mangorage.mangobotcore.api.util.misc.Arguments;
import java.util.*;
import java.util.function.Function;

public final class CommandNode implements ICommandNode {
    private final String name;
    private final String usage;
    private final Function<ICommandContext, Boolean> requiresCheck;
    private final Map<String, ICommandNode> children;
    private final ICommandExecutor executor;

    public CommandNode(String name, String usage, Function<ICommandContext, Boolean> requiresCheck, Map<String, ICommandNode> children, ICommandExecutor executor) {
        this.name = name;
        this.usage = usage;
        this.requiresCheck = requiresCheck;
        this.children = Map.copyOf(children);
        this.executor = executor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public List<ICommandNode> getSubNodes() {
        return null;
    }

    public ICommandResult execute(ICommandContext ctx, List<String> args) {
        if (!requiresCheck.apply(ctx)) {
            return CommandResult.NO_PERMISSION;
        }

        if (args.isEmpty()) {
            if (executor == null)
                throw new RuntimeException("No executor for command: " + name);
            return executor.execute(ctx, Arguments.empty());
        }

        String next = args.getFirst();
        ICommandNode child = children.get(next);

        if (child != null) {
            return child.execute(ctx, args.subList(1, args.size()));
        } else {
            if (executor == null)
                return CommandResult.FAIL;

            return executor.execute(ctx, Arguments.of(args.toArray(String[]::new)));
        }
    }
}

