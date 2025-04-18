package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.jda.command.api.ICommand;

import java.util.List;

public class PingCommand implements ICommand {
    @Override
    public List<String> commands() {
        return List.of("ping");
    }

    @Override
    public void execute(Message message) {
        message.reply("Pong!").queue();;
    }
}
