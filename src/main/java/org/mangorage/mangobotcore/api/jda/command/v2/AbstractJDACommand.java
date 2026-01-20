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

    public JDACommandType getCommandType() {
        return JDACommandType.GLOBAL;
    }

    @Override
    public final JDACommandResult getFailedResult() {
        return JDACommandResult.ERROR;
    }

    @Override
    public JDACommandResult execute(Message context, String[] arguments, CommandParseResult commandParseResult) {

        if (getCommandType() == JDACommandType.GUILD && !context.isFromGuild())
            return JDACommandResult.GUILD_ONLY;

        if (getCommandType() == JDACommandType.DM && context.isFromGuild())
            return JDACommandResult.DM_ONLY;

        if (!hasPermission(context)) {
            return JDACommandResult.NO_PERMISSION;
        }

        // Continue with normal execution
        return super.execute(context, arguments, commandParseResult);
    }
}
