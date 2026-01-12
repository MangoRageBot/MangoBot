package org.mangorage.mangobotcore.api.jda.command.v1;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.api.util.misc.Arguments;

import java.util.List;

public interface ICommand {
    String id();

    List<String> commands();
    String usage();
    CommandResult execute(Message message, Arguments arguments);
}
