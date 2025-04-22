package org.mangorage.mangobotcore.jda.event;

import net.dv8tion.jda.api.events.Event;
import net.minecraftforge.eventbus.api.event.MutableEvent;

public abstract class DiscordEvent<E extends Event> extends MutableEvent {
    private final E discordEvent;
    public DiscordEvent(E discordEvent) {
        this.discordEvent = discordEvent;
    }

    public final E getDiscordEvent() {
        return discordEvent;
    }
}
