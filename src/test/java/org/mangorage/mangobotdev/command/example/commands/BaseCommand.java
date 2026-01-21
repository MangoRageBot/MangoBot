package org.mangorage.mangobotdev.command.example.commands;

import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;
import org.mangorage.mangobotcore.api.command.v1.CommandContext;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;

public class BaseCommand extends AbstractCommand<String, Integer> {
    public BaseCommand(String name) {
        super(name);
    }

    @Override
    public Integer getFailedResult() {
        return 0;
    }

    @Override
    public Integer run(String context, CommandContext commandContext, CommandParseResult commandParseResult) throws Throwable {
        return 0;
    }


}
