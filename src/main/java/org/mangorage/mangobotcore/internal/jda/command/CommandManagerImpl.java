package org.mangorage.mangobotcore.internal.jda.command;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.api.util.misc.Arguments;
import org.mangorage.mangobotcore.api.util.misc.TaskScheduler;
import org.mangorage.mangobotcore.entrypoint.MangoBotCore;
import org.mangorage.mangobotcore.api.jda.command.v1.CommandManager;
import org.mangorage.mangobotcore.api.jda.command.v1.ICommand;
import org.mangorage.mangobotcore.api.jda.event.v1.CommandEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class CommandManagerImpl implements CommandManager {
    private final Map<String, ICommand> commands = new HashMap<>();


    @Override
    public void register(ICommand command) {
        commands.putIfAbsent(command.id(), command);
    }

    @Override
    public void handle(Message message) {
        var rawMessage = message.getContentRaw();
        var cmdPrefix = MangoBotCore.isDevMode() ? "dev!" : "!";
        var silent = false;

        if (rawMessage.startsWith("s"+cmdPrefix)) {
            cmdPrefix = "s" + cmdPrefix;
            silent = true;
        }


        if (rawMessage.startsWith(cmdPrefix)) {

            String[] command_pre = rawMessage.split(" ");
            Arguments arguments = Arguments.of(Arrays.copyOfRange(command_pre, 1, command_pre.length));

            var cmd = rawMessage.replaceFirst(cmdPrefix, "").split(" ");

            var success = false;
            for (ICommand command : commands.values()) {
                if (command.commands().contains(cmd[0])) {
                    command.execute(message, arguments).accept(message);
                    success = true;
                    break;
                }
            }
            if (!success) {
                var event = CommandEvent.BUS.fire(new CommandEvent(message, cmd[0], arguments));
                if (event.isHandled())
                    event.getResult().accept(message);
            }
            if (silent) {
                TaskScheduler.getExecutor().schedule(() -> {
                    message.delete().queue();
                }, 250, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public ICommand getCommand(String id) {
        return commands.get(id);
    }
}
