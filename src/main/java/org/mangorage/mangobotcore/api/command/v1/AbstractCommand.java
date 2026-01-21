package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.command.v1.argument.Argument;
import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;
import org.mangorage.mangobotcore.api.command.v1.argument.OptionalFlagArg;
import org.mangorage.mangobotcore.api.command.v1.argument.OptionalArg;
import org.mangorage.mangobotcore.api.command.v1.argument.RequiredArg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCommand<C, R> {
    private final Map<String, AbstractCommand<C, R>> subCommand = new LinkedHashMap<>();
    private final Map<String, Argument<?>> arguments = new LinkedHashMap<>();
    private final String name;

    private int requiredArgs = 0;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract R getFailedResult();

    protected void addSubCommand(AbstractCommand<C, R> command) {
        subCommand.put(command.getName(), command);
    }

    protected <T> RequiredArg<T> registerRequiredArgument(String name, String description, ArgumentType<T> type) {
        arguments.put(
                name,
                new RequiredArg<>(name, description, type)
        );
        requiredArgs++;
        return (RequiredArg<T>) arguments.get(name);
    }

    protected <T> OptionalFlagArg registerFlagArgument(String name, String description) {
        arguments.put(
                name,
                new OptionalFlagArg(name, description)
        );
        return (OptionalFlagArg) arguments.get(name);
    }

    protected <T> OptionalArg<T> registerOptionalArgument(String name, String description, ArgumentType<T> type) {
        arguments.put(
                name,
                new OptionalArg<>(name, description, type)
        );
        return (OptionalArg<T>) arguments.get(name);
    }

    public List<String> buildUsage() {
        return buildUsage(false);
    }

    public List<String> buildUsage(boolean advanced) {
        List<String> usages = new ArrayList<>();
        buildUsageInternal("", usages, advanced);
        return usages;
    }

    private void buildUsageInternal(String prefix, List<String> usages, boolean advanced) {
        String current = prefix.isEmpty() ? "/" + name : prefix + " " + name;

        // Recurse into subcommands
        if (!subCommand.isEmpty()) {
            for (AbstractCommand<C, R> sub : subCommand.values()) {
                sub.buildUsageInternal(current, usages, advanced);
            }
            return;
        }

        StringBuilder sb = new StringBuilder(current);

        for (Argument<?> arg : arguments.values()) {
            String renderedName = arg.getName();

            if (advanced) {
                renderedName += ":" + arg.getType().getString();
            }

            if (arg instanceof RequiredArg<?>) {
                sb.append(" <").append(renderedName).append(">");
            } else {
                sb.append(" [").append(renderedName).append("]");
            }
        }

        usages.add(sb.toString());
    }

    public R execute(C context, String[] arguments, CommandParseResult commandParseResult) {
        try {
            if (arguments.length == 0) {
                if (requiredArgs != 0) {
                    commandParseResult.addMessage("Not enough arguments! Required: " + requiredArgs + ", Provided: " + arguments.length);
                    return getFailedResult();
                }
                return run(context, CommandContext.empty(), commandParseResult);
            }

            AbstractCommand<C, R> sub = subCommand.get(arguments[0]);
            if (sub != null) {
                String[] subArgs = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, subArgs, 0, subArgs.length);
                return sub.execute(context, subArgs, commandParseResult);
            } else {
                if (arguments.length < requiredArgs) {
                    commandParseResult.addMessage("Not enough arguments! Required: " + requiredArgs + ", Provided: " + arguments.length);
                    return getFailedResult();
                }
                return run(context, CommandContext.of(arguments), commandParseResult);
            }
        } catch (Throwable throwable) {
            commandParseResult.addMessage(throwable.toString());
            return getFailedResult();
        }
    }

    public abstract R run(C context, CommandContext commandContext, CommandParseResult commandParseResult) throws Throwable;
}
