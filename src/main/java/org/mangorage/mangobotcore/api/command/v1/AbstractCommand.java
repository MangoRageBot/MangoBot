package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.command.v1.argument.Argument;
import org.mangorage.mangobotcore.api.command.v1.argument.ArgumentType;
import org.mangorage.mangobotcore.api.command.v1.argument.OptionalFlagArg;
import org.mangorage.mangobotcore.api.command.v1.argument.OptionalArg;
import org.mangorage.mangobotcore.api.command.v1.argument.RequiredArg;
import org.mangorage.mangobotcore.api.command.v1.info.CommandData;
import org.mangorage.mangobotcore.api.command.v1.info.CommandPart;
import org.mangorage.mangobotcore.api.command.v1.info.ParameterPart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCommand<C, R> {
    private final Map<String, AbstractCommand<C, R>> subCommands = new LinkedHashMap<>();
    private final Map<String, AbstractCommand<C, R>> aliasCommands = new LinkedHashMap<>();

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

    public List<String> aliases() {
        return List.of();
    }

    protected void addSubCommand(AbstractCommand<C, R> command) {
        subCommands.put(command.getName(), command);
        command.aliases().forEach(alias -> {
            aliasCommands.put(alias, command);
        });
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

    public List<String> getCommandNotes() {
        return List.of("No additional notes for this command.");
    }

    /**
     * Get a fully detailed breakdown of the command and its subcommands
     * @return CommandData containing all command parts
     */
    public CommandData buildCommandParts() {
        List<CommandPart> parts = new ArrayList<>();
        buildCommandPartsInternal("", parts);
        return new CommandData(name, parts);
    }

    private void buildCommandPartsInternal(String prefix, List<CommandPart> parts) {
        String current = prefix.isEmpty() ? "/" + name : prefix + " " + name;

        if (!subCommands.isEmpty()) {
            for (AbstractCommand<C, R> sub : subCommands.values()) {
                sub.buildCommandPartsInternal(current, parts);
            }
        }

        List<ParameterPart> params = new ArrayList<>();

        for (Argument<?> arg : arguments.values()) {
            boolean required = arg instanceof RequiredArg<?>;
            params.add(
                    new ParameterPart(
                            arg.getName(),
                            arg.getType().getString(),
                            arg.getString(),
                            arg.getDescription(),
                            String.join(", ", arg.getType().getSuggestions()),
                            required
            ));
        }

        parts.add(new CommandPart(current, aliases(), getCommandNotes(), params));
    }

    public CommandInfo buildUsage() {
        return buildUsage(false);
    }

    public CommandInfo buildUsage(boolean advanced) {
        return buildCommandParts().buildCommandInfo(advanced);
    }

    public R execute(CommandContext<C> ctx) {
        final var result = ctx.getParseResult();

        try {
            if (ctx.remaining() == 0) {
                if (requiredArgs != 0) {
                    result.addMessage("Not enough arguments! Required: " + requiredArgs + ", Provided: 0");
                    return getFailedResult();
                }
                return run(ctx);
            }

            String name = ctx.peek();
            AbstractCommand<C, R> sub = subCommands.get(name);
            if (sub == null) // Attempt to get the alias if it exists...
                sub = aliasCommands.get(name);

            if (sub != null) {
                ctx.next(); // consume subcommand token
                return sub.execute(ctx);
            }

            if (ctx.remaining() < requiredArgs) {
                result.addMessage("Not enough arguments! Required: " + requiredArgs + ", Provided: " + ctx.remaining());
                return getFailedResult();
            }

            return run(ctx);
        } catch (Throwable t) {
            result.addMessage(t.toString());
            return getFailedResult();
        }
    }

    public abstract R run(CommandContext<C> commandContext) throws Throwable;
}
