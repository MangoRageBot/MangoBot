package org.mangorage.mangobotdev.command.example;

import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.ICommandDispatcher;
import org.mangorage.mangobotdev.command.example.commands.QuoteCommand;

public final class Example {
    public static void main(String[] args) {
        ICommandDispatcher<String, Integer> dispatcher = ICommandDispatcher.create(1);
        CommandParseResult commandParseResult = new CommandParseResult();

        dispatcher.register(new QuoteCommand());
        dispatcher.execute("quote Hello! \"This is a quoted message.\"", "AdminUser", commandParseResult);
        commandParseResult.getMessages().forEach(System.out::println);

        dispatcher.getCommand("quote").buildUsage().forEach(System.out::println);
    }

}
