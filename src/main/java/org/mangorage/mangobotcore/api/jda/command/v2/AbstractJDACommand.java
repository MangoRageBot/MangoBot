package org.mangorage.mangobotcore.api.jda.command.v2;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.api.command.v1.AbstractCommand;

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
    public JDACommandResult execute(Message context, String[] arguments) {
        if (isGuildOnly() && !context.isFromGuild()) {
            return JDACommandResult.GUILD_ONLY;
        }

        if (!hasPermission(context)) {
            return JDACommandResult.NO_PERMISSION;
        }

        // Continue with normal execution
        return super.execute(context, arguments);
    }
}
