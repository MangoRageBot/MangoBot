package org.mangorage.mangobotdev.command.example;

import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;
import org.mangorage.mangobotcore.api.command.v1.argument.OptionalArg;
import org.mangorage.mangobotcore.api.command.v1.argument.RequiredArg;
import org.mangorage.mangobotcore.api.command.v1.argument.types.IntegerArgumentType;

public class KickCommand extends AbstractCommand<String, Integer> {
    private final RequiredArg<Integer> userIdArg;
    private final OptionalArg<Integer> durationArg;

    public KickCommand(String name) {
        super(name);
        this.userIdArg = registerRequiredArgument("userId", "ID of the user to kick", IntegerArgumentType.INSTANCE);
        this.durationArg = registerOptionalArgument("duration", "Duration of the kick in minutes", IntegerArgumentType.INSTANCE);
    }

    @Override
    public Integer getFailedResult() {
        return 1;
    }

    @Override
    public Integer run(String context, String[] arguments) throws Throwable {
        System.out.println("Executing kick command..." + arguments[0]);
        System.out.println("UserId: " + userIdArg.parse(arguments[0]));
        return 1;
    }
}
