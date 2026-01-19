package org.mangorage.mangobotdev.command.example;

import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;
import org.mangorage.mangobotcore.api.command.v1.argument.types.IntegerArgumentType;

public class SubCommand extends AbstractCommand<String, Integer> {
    public SubCommand(String name, boolean useArg) {
        super(name);
        if (useArg) {
            registerRequiredArgument(
                    "player",
                    "The Player",
                    IntegerArgumentType.INSTANCE
            );
        }
    }

    @Override
    public Integer getFailedResult() {
        return 0;
    }

    @Override
    public Integer run(String context, String[] arguments) throws Throwable {
        return 0;
    }
}
