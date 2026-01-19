package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.util.misc.Arguments;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommand<C, R> {
    private final Map<String, AbstractCommand<C, R>> subCommand = new HashMap<>();
    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected void addSubCommand(AbstractCommand<C, R> command) {
        subCommand.put(command.getName(), command);
    }

    public R execute(C context, String[] arguments) {
        if (arguments.length == 0) {
            return run(context, Arguments.empty());
        }

        AbstractCommand<C, R> sub = subCommand.get(arguments[0]);
        if (sub != null) {
            String[] subArgs = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, subArgs, 0, subArgs.length);
            return sub.execute(context, subArgs);
        } else {
            return run(context, Arguments.of(arguments));
        }
    }

    public abstract R run(C context, Arguments arguments);
}
