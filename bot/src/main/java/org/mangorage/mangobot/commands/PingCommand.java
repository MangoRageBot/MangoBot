package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.commonutils.misc.Arguments;
import org.mangorage.mangobotcore.jda.command.api.CommandResult;
import org.mangorage.mangobotcore.jda.command.api.ICommand;

import java.util.List;

public class PingCommand implements ICommand {
    @Override
    public List<String> commands() {
        return List.of("ping");
    }

    @Override
    public String usage() {
        return "";
    }

    @Override
    public CommandResult execute(Message message, Arguments arguments) {
        message.reply("Pong!").queue();;
        return CommandResult.PASS;
    }
}
