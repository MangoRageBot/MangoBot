/*
 * Copyright (c) 2023-2024. MangoRage
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

package org.mangorage.mangobotapi.core.classloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ClassTransformers {


    private final HashMap<String, Class<?>> CLASSES = new HashMap<>(); // Transformed Class's
    private final ArrayList<IClassTransformer> TRANSFORMERS = new ArrayList<>(); // Transformer's
    private final ClassLoader loader;

    public ClassTransformers(ClassLoader loader) {
        this.loader = loader;
    }

    public void add(String name, Class<?> cool) {
        CLASSES.put(name, cool);
    }

    public void add(IClassTransformer transformer) {
        TRANSFORMERS.add(transformer);
    }

    public boolean empty() {
        return TRANSFORMERS.isEmpty();
    }

    private byte[] getClassBytes(String clazz) {
        try {
            String className = clazz.replace('.', '/');
            String classFileName = className + ".class";

            try (var is = loader.getResourceAsStream(classFileName)) {
                if (is != null) return is.readAllBytes();
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public byte[] transform(String name) {
        byte[] originalClassData = getClassBytes(name);

        AtomicReference<TransformResult> result = new AtomicReference<>(TransformerFlags.NO_REWRITE.of(originalClassData));
        AtomicReference<IClassTransformer> _transformer = new AtomicReference<>();

        for (IClassTransformer transformer : TRANSFORMERS) {
            result.set(transformer.transform(originalClassData));
            if (result.get().flag() != TransformerFlags.NO_REWRITE) {
                _transformer.set(transformer);
                break;
            }
        }

        if (result.get().flag() != TransformerFlags.NO_REWRITE && _transformer.get() != null) {
            System.out.println("%s Transformed %s".formatted(_transformer.get().getName(), name));
            return result.get().classData();
        }

        return null;
    }

    public boolean containsClass(String name) {
        return CLASSES.containsKey(name);
    }

    public Class<?> getClazz(String string) {
        return CLASSES.get(string);
    }
}
