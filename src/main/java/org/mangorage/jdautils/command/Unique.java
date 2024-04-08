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

package org.mangorage.jdautils.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Unique {

    private static final Map<String, List<Object>> identifiers = new HashMap<>();

    /**
     * Checks if the object is unique
     *
     * @param identifier   A collection identifier to check if all objects in this collection are unique
     * @param object       The object to check
     * @param errorMessage The error message to throw if the object is not unique
     */
    public static void checkUnique(String identifier, Object object, String errorMessage) {
        if (identifiers.containsKey(identifier)) {
            if (identifiers.get(identifier).contains(object))
                throw new IllegalArgumentException(errorMessage);
            identifiers.get(identifier).add(object);
        } else {
            identifiers.computeIfAbsent(identifier, k -> new ArrayList<>()).add(object);
        }
    }
}
