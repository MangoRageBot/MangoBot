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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.mangorage.mangobotapi.core.util.APIUtil;

import java.util.HashMap;

public class VCInstance {

    @Expose
    private final String guildId;
    @Expose
    private final HashMap<String, String> channels = new HashMap<>(); // channelId -> userId
    @Expose
    private String channelId;


    protected VCInstance(String guildId) {
        this.guildId = guildId;
    }

    public void join(AudioChannelUnion audioChannelUnion, Member member) {
        var id = audioChannelUnion.getId();
        if (id.equals(channelId)) {
            var category = audioChannelUnion.getParentCategory();

            if (category == null) return;

            category.createVoiceChannel("%s's VC".formatted(member.getEffectiveName()))
                    .setPosition(audioChannelUnion.getPositionRaw() + 1)
                    .reason("Custom VC")
                    .queue(vc -> {
                        channels.put(vc.getId(), member.getId());
                        member.getGuild().moveVoiceMember(member, vc).queue();
                        CustomVC.INSTANCE_DATA_HANDLER.save(this, guildId);
                    });
        }
    }

    private boolean hasMembers(AudioChannelUnion audioChannelUnion) {
        if (audioChannelUnion.getMembers().size() == 1) {
            return !audioChannelUnion.getMembers().get(0).getUser().isBot();
        }
        return !audioChannelUnion.getMembers().isEmpty();
    }

    public void leave(AudioChannelUnion audioChannelUnion, Member member) {
        var id = audioChannelUnion.getId();
        if (channels.containsKey(id) && !hasMembers(audioChannelUnion)) { // check if empty!
            audioChannelUnion.delete().queue(after -> {
                channels.remove(id);
                CustomVC.INSTANCE_DATA_HANDLER.save(this, guildId);
            });
        }
    }

    protected void setChannelId(String channelId) {
        this.channelId = channelId;
        CustomVC.INSTANCE_DATA_HANDLER.save(this, guildId);
    }

    protected String getGuildID() {
        return guildId;
    }

    protected boolean isOwner(AudioChannelUnion voice, Member member) {
        return channels.containsKey(voice.getId()) && channels.get(voice.getId()).equals(member.getId());
    }

    protected boolean isOwner(Member member) {
        if (APIUtil.inVC(member)) {
            var voice = member.getVoiceState();
            if (voice == null) return false;
            var channel = voice.getChannel();
            if (channel == null) return false;
            return isOwner(channel, member);
        }
        return false;
    }
}
