/*
 * Copyright (c) 2024. MangoRage
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

package org.mangorage.mangobotapi.core.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ties a List to an object. Used for Aliases for commands.
 */
public final class CommandAlias {
    private static final Map<Object, List<String>> ALIASES = new HashMap<>();

    /**
     * Pass thru strings to get a list back. Creates the list if it doesnt already exist.
     * Pass an Object as the key, can be a class, or the instance of the command. or anything.
     * <p>
     * Recommended to only pass through the Class or Object instance of the command in question.
     *
     * @param object
     * @param aliases
     * @return
     */

    public static List<String> create(Object object, String... aliases) {
        return ALIASES.computeIfAbsent(object, c -> List.of(aliases));
    }
}
