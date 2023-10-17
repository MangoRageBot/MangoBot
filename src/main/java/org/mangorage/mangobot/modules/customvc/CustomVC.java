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

import com.google.gson.annotations.Expose;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.data.DataHandler;
import org.mangorage.mangobotapi.core.events.discord.DVoiceUpdateEvent;

import java.util.HashMap;
import java.util.HashSet;

public class CustomVC {
    private static final HashMap<String, Instance> INSTANCES = new HashMap<>(); // guildID -> Inst

    private static final DataHandler<Instance> INSTANCE_DATA_HANDLER = DataHandler.create(
            (inst) -> {
                INSTANCES.put(inst.guildId, inst);
            },
            Instance.class,
            "data/customvc/",
            DataHandler.Properties.create()
                    .useExposeAnnotation()
                    .setFileName("instance.json")
                    .useDefaultFileNamePredicate()
    );


    private static void onJoin(DVoiceUpdateEvent event) {
        var dEvent = event.get();
        var guild = dEvent.getGuild();
        var channelJoined = dEvent.getChannelJoined();
        var channelLeft = dEvent.getChannelLeft();

        var instance = INSTANCES.computeIfAbsent(guild.getId(), Instance::new);
        if (channelJoined != null)
            instance.join(channelJoined, dEvent.getMember());
        if (channelLeft != null)
            instance.leave(channelLeft, dEvent.getMember());
    }


    public static void init() {
        Bot.EVENT_BUS.addListener(DVoiceUpdateEvent.class, CustomVC::onJoin);
        INSTANCE_DATA_HANDLER.loadAll();
    }

    public static void configure(Guild guild, String channelId) {
        var instance = INSTANCES.computeIfAbsent(guild.getId(), Instance::new);
        instance.channelId = channelId;
    }


    public static class Instance {

        @Expose
        private final String guildId;
        @Expose
        private final HashSet<String> channels = new HashSet<>();
        @Expose
        private String channelId;


        private Instance(String guildId) {
            this.guildId = guildId;
        }

        public void join(AudioChannelUnion audioChannelUnion, Member member) {
            var id = audioChannelUnion.getId();
            if (id.equals(channelId)) {
                var category = audioChannelUnion.getParentCategory();

                if (category == null) return;

                category.createVoiceChannel("%s".formatted(member.getEffectiveName())).queue(vc -> {
                    channels.add(vc.getId());
                    member.getGuild().moveVoiceMember(member, vc).queue();
                    INSTANCE_DATA_HANDLER.save(this, guildId);
                });
            }
        }

        public void leave(AudioChannelUnion audioChannelUnion, Member member) {
            var id = audioChannelUnion.getId();
            if (channels.contains(id) && audioChannelUnion.getMembers().isEmpty()) { // check if empty!
                audioChannelUnion.delete().queue(after -> {
                    channels.remove(id);
                    INSTANCE_DATA_HANDLER.save(this, guildId);
                });
            }
        }
    }

}
