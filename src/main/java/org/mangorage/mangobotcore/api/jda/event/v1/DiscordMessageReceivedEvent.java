package org.mangorage.mangobotcore.api.jda.event.v1;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;

public final class DiscordMessageReceivedEvent extends DiscordEvent<MessageReceivedEvent> {
    public static final EventBus<DiscordMessageReceivedEvent> BUS = EventBus.create(DiscordMessageReceivedEvent.class);

    public DiscordMessageReceivedEvent(MessageReceivedEvent discordEvent) {
        super(discordEvent);
    }
}
