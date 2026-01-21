package org.mangorage.mangobotdev.command.example.commands;

import org.mangorage.mangobotcore.api.command.v1.CommandContext;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;
import org.mangorage.mangobotcore.api.command.v1.argument.OptionalFlagArg;
import org.mangorage.mangobotcore.api.command.v1.argument.RequiredArg;
import org.mangorage.mangobotcore.api.command.v1.argument.types.StringArgumentType;

public final class QuoteCommand extends BaseCommand {
    private final RequiredArg<String> msgArg;
    private final RequiredArg<String> quoteArg;
    private final OptionalFlagArg shoutArg;

    public QuoteCommand() {
        super("quote");
        this.msgArg = registerRequiredArgument(
            "message",
            "message",
            StringArgumentType.single()
        );
        this.shoutArg = registerFlagArgument(
            "--shout",
            "shout the quote"
        );
        this.quoteArg = registerRequiredArgument(
            "quote",
            "quoted message",
            StringArgumentType.quote()
        );
    }

    @Override
    public Integer run(String context, CommandContext commandContext, CommandParseResult commandParseResult) {
        System.out.println(
                commandContext.getArgument(msgArg, commandParseResult)
        );
        System.out.println(
                commandContext.getArgument(shoutArg, commandParseResult)
        );
        System.out.println(
                commandContext.getArgument(quoteArg, commandParseResult)
        );
        System.out.println("Whelp!");
        return 1;
    }
}
