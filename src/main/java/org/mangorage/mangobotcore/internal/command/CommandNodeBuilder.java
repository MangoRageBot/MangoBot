package org.mangorage.mangobotcore.internal.command;

import org.mangorage.mangobotcore.api.command.v1.ICommandContext;
import org.mangorage.mangobotcore.api.command.v1.ICommandExecutor;
import org.mangorage.mangobotcore.api.command.v1.ICommandNode;
import org.mangorage.mangobotcore.api.command.v1.ICommandNodeBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CommandNodeBuilder implements ICommandNodeBuilder {
    private final String name;
    private final Map<String, ICommandNode> children = new HashMap<>();
    private String usage;
    private ICommandExecutor executor;
    private Function<ICommandContext, Boolean> requires = c -> true;

    public CommandNodeBuilder(String name) {
        this.name = name;
    }

    @Override
    public ICommandNodeBuilder usage(String usage) {
        this.usage = usage;
        return this;
    }

    public ICommandNodeBuilder then(ICommandNode child) {
        children.put(child.getName(), child);
        return this;
    }

    public ICommandNodeBuilder executes(ICommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public ICommandNode build() {
        return new CommandNode(
                name,
                usage,
                requires,
                children,
                executor
        );
    }

    public ICommandNodeBuilder requires(Function<ICommandContext, Boolean> requires) {
        this.requires = requires;
        return this;
    }
}
