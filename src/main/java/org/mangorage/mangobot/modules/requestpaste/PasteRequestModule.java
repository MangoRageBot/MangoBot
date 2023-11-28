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

package org.mangorage.mangobot.modules.requestpaste;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.mangorage.basicutils.TaskScheduler;
import org.mangorage.basicutils.misc.LazyReference;
import org.mangorage.mangobot.Core;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;
import org.mangorage.mangobotapi.core.events.discord.DReactionEvent;
import org.mangorage.mboteventbus.impl.IEventBus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class PasteRequestModule {
    private static final LazyReference<GitHubClient> GITHUB_CLIENT = LazyReference.create(() -> new GitHubClient().setOAuth2Token(Core.PASTE_TOKEN.get()));
    private static final List<String> GUILDS = List.of(
            "1129059589325852724",
            "834300742864601088"
    );
    private static final Emoji EMOJI = Emoji.fromUnicode("\uD83D\uDCCB");

    public static void register(IEventBus bus) {
        bus.addListener(DMessageRecievedEvent.class, PasteRequestModule::onMessage);
        bus.addListener(DReactionEvent.class, PasteRequestModule::onReact);
    }

    private static byte[] getData(InputStream stream) {
        try {
            byte[] data = stream.readAllBytes();
            stream.close();
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    private static String getFileName(Message.Attachment attachment, int count) {
        var fileName = attachment.getFileName();
        var ext = ".%s".formatted(attachment.getFileExtension());
        if (ext == null) return attachment.getFileName();
        var fileNameNoExt = fileName.substring(0, fileName.length() - ext.length());
        return "%s_%s%s".formatted(fileNameNoExt, count, ext);
    }

    private static boolean containsPrintableCharacters(String input) {
        // Use a regular expression to match all printable characters, including colon and semicolon
        return Pattern.compile("[\\x20-\\x7E:;]+").matcher(input.substring(0, Math.min(input.length(), 5))).matches();
    }

    public static void createGists(Message msg) {
        TaskScheduler.getExecutor().execute(() -> {
            if (!msg.isFromGuild()) return;
            if (!GUILDS.contains(msg.getGuildId())) return;

            var attachments = msg.getAttachments();
            if (attachments.isEmpty()) return;

            GitHubClient CLIENT = GITHUB_CLIENT.get();
            GistService service = new GistService(CLIENT);
            AtomicInteger count = new AtomicInteger(1);

            Gist gist = new Gist();
            gist.setPublic(false);
            gist.setDescription("Automatically made from MangoBot.");

            HashMap<String, GistFile> FILES = new HashMap<>();
            attachments.forEach(attachment -> {
                try {

                    byte[] bytes = getData(attachment.getProxy().download().get());
                    if (bytes == null) return;
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    if (!containsPrintableCharacters(content)) return;
                    var fileName = getFileName(attachment, count.getAndAdd(1));

                    var gistFile = new GistFile();
                    gistFile.setContent(content);
                    gistFile.setFilename(fileName);

                    FILES.put(fileName, gistFile);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            gist.setFiles(FILES);

            try {
                var remote = service.createGist(gist);
                msg.reply("gist -> %s".formatted(remote.getHtmlUrl())).setSuppressEmbeds(true).mentionRepliedUser(false).queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public static void onMessage(DMessageRecievedEvent event) {
        var dEvent = event.get();
        var message = dEvent.getMessage();
        var attachments = message.getAttachments();
        if (!attachments.isEmpty()) {
            TaskScheduler.getExecutor().execute(() -> {
                var suceeeded = new AtomicBoolean(false);
                for (Message.Attachment attachment : attachments) {
                    try {
                        byte[] bytes = getData(attachment.getProxy().download().get());
                        if (bytes == null) continue;
                        String content = new String(bytes, StandardCharsets.UTF_8);
                        if (containsPrintableCharacters(content)) {
                            suceeeded.set(true);
                            break;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (suceeeded.get()) message.addReaction(EMOJI).queue();
            });
        }
    }

    public static void onReact(DReactionEvent event) {
        var dEvent = event.get();

        if (!dEvent.isFromGuild()) return;
        if (dEvent.getUser() == null) return;
        if (dEvent.getUser().isBot()) return;

        dEvent.retrieveMessage().queue(a -> {
            if (a.getAttachments().isEmpty()) return;
            a.retrieveReactionUsers(EMOJI).queue(b -> {
                b.stream().filter(user -> user.getId().equals(dEvent.getJDA().getSelfUser().getId())).findFirst().ifPresent(c -> {
                    a.clearReactions(EMOJI).queue();
                    createGists(a);
                });
            });
        });

    }
}
