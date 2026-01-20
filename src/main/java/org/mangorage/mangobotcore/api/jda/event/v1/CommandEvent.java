package org.mangorage.mangobotcore.api.jda.event.v1;

import net.dv8tion.jda.api.entities.Message;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.mangorage.mangobotcore.api.jda.command.v2.JDACommandResult;
import org.mangorage.mangobotcore.api.util.misc.Arguments;

public final class CommandEvent extends MutableEvent {
    public static final EventBus<CommandEvent> BUS = EventBus.create(CommandEvent.class);

    private final Message message;
    private final String cmd;
    private final Arguments arguments;

    private JDACommandResult result = null;

    public CommandEvent(Message message, String cmd, Arguments arguments) {
        this.message = message;
        this.cmd = cmd;
        this.arguments = arguments;
    }

    public Message getMessage() {
        return message;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public String getCommand() {
        return cmd;
    }

    public void setHandled(JDACommandResult result) {
        this.result = result;
    }

    public boolean isHandled() {
        return result != null;
    }

    public JDACommandResult getResult() {
        return result;
    }
}
