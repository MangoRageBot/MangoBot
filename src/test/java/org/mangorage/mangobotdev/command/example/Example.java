package org.mangorage.mangobotdev.command.example;

import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.ICommandDispatcher;

public final class Example {
    public static void main(String[] args) {
        ICommandDispatcher<String, Integer> dispatcher = ICommandDispatcher.create(1);
        CommandParseResult commandParseResult = new CommandParseResult();

        final var kickCMD = new KickCommand("kick");
        final var subCMD = new CommandWithSubCommand("hello");

        dispatcher.register(kickCMD);
        dispatcher.execute("kick 1", "AdminUser", commandParseResult);

        commandParseResult.getMessages().forEach(System.out::println);
        kickCMD.buildUsage().forEach(System.out::println);
        subCMD.buildUsage().forEach(System.out::println);
    }

}
