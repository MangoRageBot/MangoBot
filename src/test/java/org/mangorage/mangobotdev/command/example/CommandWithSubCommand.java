package org.mangorage.mangobotdev.command.example;

import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;

public class CommandWithSubCommand extends AbstractCommand<String, Integer> {
    public CommandWithSubCommand(String name) {
        super(name);
        this.addSubCommand(new SubCommand("add", true));
        this.addSubCommand(new SubCommand("remove", true));
        this.addSubCommand(new SubCommand("list", false));
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
