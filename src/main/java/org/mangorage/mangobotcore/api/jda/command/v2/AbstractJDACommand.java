package org.mangorage.mangobotcore.api.jda.command.v2;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;
import org.mangorage.mangobotcore.api.command.v1.CommandParseResult;

public abstract class AbstractJDACommand extends AbstractCommand<Message, JDACommandResult> {

    public AbstractJDACommand(String name) {
        super(name);
    }

    public boolean hasPermission(Message context) {
        return true;
    }

    public boolean isGuildOnly() {
        return false;
    }

    @Override
    public final JDACommandResult getFailedResult() {
        return JDACommandResult.FAIL;
    }

    @Override
    public JDACommandResult execute(Message context, String[] arguments, CommandParseResult commandParseResult) {
        if (isGuildOnly() && !context.isFromGuild()) {
            return JDACommandResult.GUILD_ONLY;
        }

        if (!hasPermission(context)) {
            return JDACommandResult.NO_PERMISSION;
        }

        // Continue with normal execution
        return super.execute(context, arguments, commandParseResult);
    }
}
