package org.mangorage.mangobotcore.jda.command.api;

import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public interface ICommand {
    List<String> commands();
    void execute(Message message);
}
