package org.mangorage.mangobotcore.internal.command;

import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.ICommandDispatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommandDispatcher<C, R> implements ICommandDispatcher<C, R> {
    private final Map<String, AbstractCommand<C, R>> roots = new HashMap<>();
    private final R defaultInvalid;

    public CommandDispatcher(R defaultInvalid) {
        this.defaultInvalid = defaultInvalid;
    }

    @Override
    public void register(AbstractCommand<C, R> commandNode) {
        roots.put(
                commandNode.getName(),
                commandNode
        );
    }

    @Override
    public AbstractCommand<C, R> getCommand(String name) {
        return roots.get(name);
    }

    @Override
    public List<AbstractCommand<C, R>> getAllRegisteredCommands() {
        return roots.values().stream().toList();
    }

    @Override
    public R execute(String input, C context, CommandParseResult commandParseResult) {
        String[] split = input.trim().split("\\s+");
        if (split.length == 0)
            return defaultInvalid;

        AbstractCommand<C, R> root = roots.get(split[0]);
        if (root == null)
            return defaultInvalid;

        return root.execute(context, Arrays.copyOfRange(split, 1, split.length), commandParseResult);
    }
}
