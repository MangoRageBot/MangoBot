package org.mangorage.mangobotcore.jda.event;

import net.dv8tion.jda.api.entities.Message;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.mangorage.commonutils.misc.Arguments;
import org.mangorage.mangobotcore.jda.command.api.CommandResult;

public final class CommandEvent extends MutableEvent {
    public static final EventBus<CommandEvent> BUS = EventBus.create(CommandEvent.class);

    private final Message message;
    private final String cmd;
    private final Arguments arguments;

    private boolean handled = false;

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

    public void setHandled(CommandResult execute) {
        this.handled = true;
    }

    public boolean isHandled() {
        return handled;
    }
}
