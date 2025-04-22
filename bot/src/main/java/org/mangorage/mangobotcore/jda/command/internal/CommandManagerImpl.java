package org.mangorage.mangobotcore.jda.command.internal;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.commonutils.misc.Arguments;
import org.mangorage.mangobotcore.jda.command.api.CommandManager;
import org.mangorage.mangobotcore.jda.command.api.ICommand;
import org.mangorage.mangobotcore.jda.event.CommandEvent;

import java.util.ArrayList;
import java.util.List;

public final class CommandManagerImpl implements CommandManager {
    private final List<ICommand> commands = new ArrayList<>();


    @Override
    public void register(ICommand command) {
        commands.add(command);
    }

    @Override
    public void handle(Message message) {
        var rawMessage = message.getContentRaw();
        var cmdPrefix = "?";
        if (rawMessage.startsWith(cmdPrefix)) {

            String[] command_pre = rawMessage.split(" ");
            Arguments arguments = Arguments.of(Arguments.of(command_pre).getFrom(1).split(" "));

            var cmd = rawMessage.replaceFirst("\\"+cmdPrefix, "").split(" ");

            var success = false;
            for (ICommand command : commands) {
                if (command.commands().contains(cmd[0])) {
                    command.execute(message, arguments);
                    success = true;
                    break;
                }
            }
            if (!success)
                CommandEvent.BUS.post(new CommandEvent(message, cmd[0], arguments));
        }
    }
}
