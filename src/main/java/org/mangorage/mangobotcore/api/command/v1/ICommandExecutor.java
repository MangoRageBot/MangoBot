package org.mangorage.mangobotcore.api.command.v1;

import org.mangorage.mangobotcore.api.util.misc.Arguments;

public interface ICommandExecutor {
    ICommandResult execute(ICommandContext context, Arguments arguments);
}
