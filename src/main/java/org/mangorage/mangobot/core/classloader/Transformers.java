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

package org.mangorage.mangobot.core.classloader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Transformers {
    public static ClassNode getClassNode(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);
        return classNode;
    }

    public static byte[] getClassBytes(ClassNode classNode) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }


    private final HashMap<String, Class<?>> CLASSES = new HashMap<>(); // Transformed Class's
    private final ArrayList<ITransformer> TRANSFORMERS = new ArrayList<>(); // Transformer's
    private final ClassLoader loader;

    public Transformers(ClassLoader loader) {
        this.loader = loader;
    }

    public void add(String name, Class<?> cool) {
        CLASSES.put(name, cool);
    }

    public void add(ITransformer transformer) {
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
        ClassNode classNode = getClassNode(getClassBytes(name));

        AtomicReference<TransformerFlags> result = new AtomicReference<>(TransformerFlags.NO_REWRITE);
        AtomicReference<ITransformer> _transformer = new AtomicReference<>();

        for (ITransformer transformer : TRANSFORMERS) {
            result.set(transformer.transform(classNode, Type.getObjectType(name)));
            if (result.get() != TransformerFlags.NO_REWRITE) {
                _transformer.set(transformer);
                break;
            }
        }

        if (result.get() != TransformerFlags.NO_REWRITE && _transformer.get() != null) {
            System.out.println("%s Transformed %s".formatted(_transformer.get().getName(), name));
            return getClassBytes(classNode);
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
