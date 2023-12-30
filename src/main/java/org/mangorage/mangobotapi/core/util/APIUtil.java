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

package org.mangorage.mangobotapi.core.util;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class APIUtil {
    public static boolean isValidBotToken(String token) {
        /**
        var bot = JDABuilder.createLight(token);
        try {
            bot.build();
        } catch (InvalidTokenException ignored) {
            return false;
        }
         **/
        return true;
    }

    @SuppressWarnings("all")
    public static List<File> getFilesInDir(String dir) {
        File file = new File(dir);
        if (file.isDirectory() && file.listFiles() != null)
            return Arrays.asList(file.listFiles());
        return List.of();
    }

    public static void saveObjectToFile(Gson gson, Object object, String directory, String fileName) {
        try {
            String jsonData = gson.toJson(object);

            File dirs = new File(directory);
            if (!dirs.exists() && !dirs.mkdirs()) return;
            Files.writeString(Path.of("%s/%s".formatted(directory, fileName)), jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFile(String directory, String fileName) {
        try {
            Files.delete(Path.of("%s/%s".formatted(directory, fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadJsonToObject(Gson gson, String file, Class<T> cls) {
        try {
            return gson.fromJson(Files.readString(Path.of(file)), cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean inVC(Member member) {
        return member.getVoiceState() != null && member.getVoiceState().inAudioChannel();
    }

    public static @Nullable AudioChannelUnion getVoiceChannel(Member member) {
        if (member == null) return null;
        var state = member.getVoiceState();
        if (state == null) return null;
        if (!state.inAudioChannel()) return null;
        return state.getChannel();
    }

    public static Optional<AudioChannelUnion> getLazyVoiceChannel(Member member) {
        return Optional.ofNullable(getVoiceChannel(member));
    }

    public static @Nullable AudioChannelManager<?, ?> getAudioChannelManager(Member member) {
        var vc = getVoiceChannel(member);
        if (vc == null) return null;
        return vc.getManager();
    }

    public static Optional<AudioChannelManager<?, ?>> getLazyAudioChannelManager(Member member) {
        return Optional.ofNullable(getAudioChannelManager(member));
    }
}
