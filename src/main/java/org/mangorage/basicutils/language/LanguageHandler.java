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

package org.mangorage.basicutils.language;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageHandler {
    private static final ConcurrentHashMap<LanguageType, LanguageLocals> LANGUAGES = new ConcurrentHashMap<>();
    private static final LanguageType DEFAULT_LANGUAGE = LanguageType.getLanguageType("en_us");
    private static LanguageType CURRENT_LANGUAGE = DEFAULT_LANGUAGE;
    private static boolean useFallbacklanguage = false;

    public static void loadAll() {
        LANGUAGES.clear();
        LanguageType.getLanguages().forEach(languageType -> {
            try {
                var lang = new LanguageLocals(languageType);
                lang.load();
                LANGUAGES.put(languageType, lang);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void load(LanguageType languageType) {
        try {
            LANGUAGES.computeIfAbsent(languageType, LanguageLocals::new).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUseFallbacklanguage(boolean value) {
        useFallbacklanguage = value;
    }

    public static void setCurrentLanguage(LanguageType language) {
        Objects.requireNonNull(language);
        CURRENT_LANGUAGE = language;
    }

    public static LanguageLocals getLanguageLocals(LanguageType languageType) {
        return getLanguageLocals(languageType, useFallbacklanguage);
    }

    public static LanguageLocals getLanguageLocals(LanguageType languageType, boolean useFallbacklanguage) {
        var locals = LANGUAGES.get(languageType);
        if (useFallbacklanguage && locals == null)
            return LANGUAGES.get(DEFAULT_LANGUAGE);
        return locals;
    }

    public static String translatable(String key) {
        return translatable(CURRENT_LANGUAGE, key);
    }

    public static String translatable(LanguageType language, String key) {
        var locals = getLanguageLocals(language);
        if (locals == null) return key;
        var value = locals.get(key);
        return value != null ? value : key;
    }
}
