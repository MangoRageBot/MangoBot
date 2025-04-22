package org.mangorage.mangobotcore.jda.event;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;

public final class DiscordMessageReactionAddEvent extends DiscordEvent<MessageReactionAddEvent> {
    public static final EventBus<DiscordMessageReactionAddEvent> BUS = EventBus.create(DiscordMessageReactionAddEvent.class);

    public DiscordMessageReactionAddEvent(MessageReactionAddEvent discordEvent) {
        super(discordEvent);
    }
}
