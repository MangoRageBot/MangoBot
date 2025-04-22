package org.mangorage.mangobotcore.jda.command.api;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.jda.command.internal.CommandManagerImpl;

public interface CommandManager {
    static CommandManager create() {
        return new CommandManagerImpl();
    }

    void register(ICommand command);
    void handle(Message message);
}
