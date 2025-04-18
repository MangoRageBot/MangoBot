package org.mangorage.mangobot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.mangorage.mangobotcore.jda.command.api.CommandManager;

public final class BotEventListener {
    private final MangoBot mangoBot;

    public BotEventListener(MangoBot mangoBot) {
        this.mangoBot = mangoBot;
    }


    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        mangoBot.getCommandManager().handle(event.getMessage());
    }
}
