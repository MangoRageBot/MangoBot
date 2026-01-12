package org.mangorage.mangobotcore.api.jda.event.v1;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.minecraftforge.eventbus.api.bus.EventBus;

public final class DiscordButtonInteractEvent extends DiscordEvent<ButtonInteractionEvent> {
    public static final EventBus<DiscordButtonInteractEvent> BUS = EventBus.create(DiscordButtonInteractEvent.class);

    public DiscordButtonInteractEvent(ButtonInteractionEvent discordEvent) {
        super(discordEvent);
    }
}
