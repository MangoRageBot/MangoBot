package org.mangorage.mangobotcore.api.jda.command.v1;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotcore.internal.jda.command.CommandManagerImpl;

public sealed interface CommandManager permits CommandManagerImpl {
    static CommandManager create() {
        return new CommandManagerImpl();
    }

    void register(ICommand command);
    void handle(Message message);
    ICommand getCommand(String id);
}
