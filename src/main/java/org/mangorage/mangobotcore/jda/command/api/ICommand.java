package org.mangorage.mangobotcore.jda.command.api;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.commonutils.misc.Arguments;

import java.util.List;

public interface ICommand {
    List<String> commands();
    String usage();
    CommandResult execute(Message message, Arguments arguments);
}
