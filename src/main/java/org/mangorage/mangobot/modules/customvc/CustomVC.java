/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobot.modules.customvc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.data.DataHandler;
import org.mangorage.mangobotapi.core.events.discord.DVoiceUpdateEvent;

import java.util.HashMap;

public class CustomVC {
    private static final HashMap<String, VCInstance> INSTANCES = new HashMap<>(); // guildID -> Inst

    protected static final DataHandler<VCInstance> INSTANCE_DATA_HANDLER = DataHandler.create(
            (inst) -> {
                INSTANCES.put(inst.getGuildID(), inst);
            },
            VCInstance.class,
            "data/customvc/guilds/",
            DataHandler.Properties.create()
                    .useExposeAnnotation()
                    .setFileName("instance.json")
                    .useDefaultFileNamePredicate()
    );


    private static void onUpdate(DVoiceUpdateEvent event) {
        var dEvent = event.get();
        var guild = dEvent.getGuild();
        var channelJoined = dEvent.getChannelJoined();
        var channelLeft = dEvent.getChannelLeft();

        var instance = INSTANCES.computeIfAbsent(guild.getId(), VCInstance::new);
        if (channelJoined != null)
            instance.join(channelJoined, dEvent.getMember());
        if (channelLeft != null)
            instance.leave(channelLeft, dEvent.getMember());
    }


    public static void init() {
        Bot.EVENT_BUS.addListener(DVoiceUpdateEvent.class, CustomVC::onUpdate);
        INSTANCE_DATA_HANDLER.loadAll();
    }

    public static void configure(Guild guild, String channelId) {
        var instance = INSTANCES.computeIfAbsent(guild.getId(), VCInstance::new);
        instance.setChannelId(channelId);
    }

    public static boolean isOwner(Guild guild, Member member) {
        var instance = INSTANCES.computeIfAbsent(guild.getId(), VCInstance::new);
        return instance.isOwner(member);
    }

    public static void setBitrate(Guild guild, Member member, int bitrate) {
        var instance = INSTANCES.computeIfAbsent(guild.getId(), VCInstance::new);
        instance.setBitrate(member, bitrate);
    }
}
