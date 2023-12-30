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

package org.mangorage.mangobotapi.core.reflection;

import java.util.Arrays;

public class ReflectionUtils {


    @SuppressWarnings("all")
    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, new Class<?>[]{}, new Object[]{});
    }

    @SuppressWarnings("all")
    public static <T> T newInstance(Class<T> clazz, Class<?>[] types, Object[] args) {
        try {
            if (types.length > 0)
                return (T) clazz.getConstructor(types).newInstance(args);
            return (T) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of " + clazz.getName() + " with args " + Arrays.toString(args), e);
        }
    }
}
