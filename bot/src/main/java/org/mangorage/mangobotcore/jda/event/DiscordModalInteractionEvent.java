package org.mangorage.mangobotcore.jda.event;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;

public class DiscordModalInteractionEvent extends DiscordEvent<ModalInteractionEvent> {

    public static final EventBus<DiscordModalInteractionEvent> BUS = EventBus.create(DiscordModalInteractionEvent.class);

    public DiscordModalInteractionEvent(ModalInteractionEvent discordEvent) {
        super(discordEvent);
    }
}
