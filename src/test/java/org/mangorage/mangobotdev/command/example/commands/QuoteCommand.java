package org.mangorage.mangobotdev.command.example.commands;

import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.argument.RequiredArg;
import org.mangorage.mangobotcore.api.command.v1.argument.types.StringArgumentType;

public final class QuoteCommand extends BaseCommand {
    private final RequiredArg<String> msgArg;
    private final RequiredArg<String> quoteArg;

    public QuoteCommand() {
        super("quote");
        this.msgArg = registerRequiredArgument(
            "message",
            "message",
            StringArgumentType.single()
        );
        this.quoteArg = registerRequiredArgument(
            "quote",
            "quoted message",
            StringArgumentType.quote()
        );
    }

    @Override
    public Integer run(String context, String[] arguments, CommandParseResult commandParseResult) {
        System.out.println(
                msgArg.get(
                        arguments, commandParseResult
                )
        );
        System.out.println(
                quoteArg.get(
                        arguments, commandParseResult
                )
        );
        System.out.println("Whelp!");
        return 1;
    }
}
