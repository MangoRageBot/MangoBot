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

package org.mangorage.mangobot.modules.translate;

import com.deepl.api.DeepLException;
import com.deepl.api.Translator;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.mangorage.basicutils.LogHelper;
import org.mangorage.mangobot.Core;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;

import java.util.concurrent.CompletableFuture;

public class Translate {
    private final Core core;
    private final Translator translator;
    private final LanguageDetector detector = LanguageDetectorBuilder
            .fromAllSpokenLanguages()
            .withPreloadedLanguageModels()
            .build();

    public Translate(Core core) {
        this.core = core;
        this.translator = new Translator(Core.DEEPL_TOKEN.get());
    }

    public void register() {
        core.getPluginBus().addListener(DMessageRecievedEvent.class, this::onMessage);
    }


    /**
     * Returns true if English or is No language at all, such as emojis. or Random characters like .@#$%^&
     *
     * @param input
     * @return
     */
    public boolean isEnglish(String input) {
        var result = detector.computeLanguageConfidenceValues(input);

        if (result.containsKey(Language.ENGLISH)) {
            var confidence = result.get(Language.ENGLISH);
            LogHelper.info("Confidence of Text being English (0.0 - 1.0) : " + confidence);
            return confidence >= 0.7;
        }

        return result.isEmpty();
    }

    public void onMessage(DMessageRecievedEvent event) {
        if (!event.isCommand()) {
            if (event.get().getAuthor().isBot()) return;
            var message = event.get().getMessage();
            String text = message.getContentRaw();

            CompletableFuture.runAsync(() -> {
                if (!isEnglish(text)) {
                    try {
                        var result = translator.translateText(text, null, "en-US");

                        if (result.getDetectedSourceLanguage().equals("en")) {
                            LogHelper.warn("Attempted to translate Text into English");
                            return;
                        }

                        StringBuilder builder = new StringBuilder();
                        builder.append("Translated Message (%s to English)".formatted(result.getDetectedSourceLanguage())).append("\n").append("\n").append(result.getText());

                        message.reply(builder).mentionRepliedUser(false).queue();
                    } catch (DeepLException | InterruptedException e) {
                        LogHelper.warn("Ran into an Exception -> " + e.getMessage());
                    }
                }
            });
        }
    }
}
