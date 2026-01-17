package org.mangorage.mangobotcore.api.command.v1;

import java.util.function.Function;

public interface ICommandNodeBuilder {
    ICommandNodeBuilder usage(String usage);
    ICommandNodeBuilder then(ICommandNode node);
    ICommandNodeBuilder requires(Function<ICommandContext, Boolean> requiresChecker);
    ICommandNodeBuilder executes(ICommandExecutor executor);

    ICommandNode build();
}
