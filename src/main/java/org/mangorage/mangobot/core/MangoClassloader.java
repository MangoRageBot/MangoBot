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

package org.mangorage.mangobot.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MangoClassloader extends URLClassLoader {
    public static ClassNode getClassNode(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);
        return classNode;
    }

    public static byte[] getClassBytes(ClassNode classNode) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(cw);
        return cw.toByteArray();
    }

    private final HashMap<String, Class<?>> CLASSES = new HashMap<>(); // Transformed Class's
    private final ArrayList<ITransformer> TRANSFORMERS = new ArrayList<>(); // Transformer's


    public MangoClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        transform();
    }

    private byte[] getClassBytes(String clazz) {
        try {
            String className = clazz.replace('.', '/');
            String classFileName = className + ".class";

            try (var is = getResourceAsStream(classFileName)) {
                if (is != null) return is.readAllBytes();
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private void transform() {
        try {
            var transformConfigs = findResources(".mtransform");
            while (transformConfigs.hasMoreElements()) {
                var config = transformConfigs.nextElement();
                try (var stream = config.openStream()) {
                    var ir = new InputStreamReader(stream);
                    var br = new BufferedReader(ir);
                    String line;
                    while ((line = br.readLine()) != null) {
                        try {
                            var transformerClass = Class.forName(line);
                            if (ITransformer.class.isAssignableFrom(transformerClass)) {
                                TRANSFORMERS.add((ITransformer) transformerClass.getConstructor().newInstance());
                                System.out.println("Found and loaded transformer %s".formatted(line));
                            }
                        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                                 IllegalAccessException | NoSuchMethodException e) {
                            System.out.println("Failed to load transformer %s".formatted(line));
                        }
                    }
                    ir.close();
                    br.close();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load any Transformers!");
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (CLASSES.containsKey(name))
            return CLASSES.get(name);
        if (TRANSFORMERS.isEmpty())
            return super.findClass(name);

        byte[] originalBytes = getClassBytes(name);

        if (originalBytes == null) {
            throw new ClassNotFoundException("Failed to load original class bytes for " + name);
        }

        ClassNode classNode = getClassNode(getClassBytes(name));

        AtomicReference<TransformerFlags> result = new AtomicReference<>(MangoClassloader.TransformerFlags.NO_REWRITE);
        AtomicReference<MangoClassloader.ITransformer> _transformer = new AtomicReference<>();

        // System.out.println("Attempting to transform: %s".formatted(name));
        for (MangoClassloader.ITransformer transformer : TRANSFORMERS) {
            result.set(transformer.transform(classNode, Type.getObjectType(name)));
            if (result.get() != MangoClassloader.TransformerFlags.NO_REWRITE) {
                _transformer.set(transformer);
                break;
            }
        }

        if (result.get() != MangoClassloader.TransformerFlags.NO_REWRITE && _transformer.get() != null) {
            System.out.println("%s Transformed %s".formatted(_transformer.get().getName(), name));

            byte[] transformedBytes = getClassBytes(classNode);
            Class<?> transformedClass = defineClass(name, transformedBytes);
            CLASSES.put(name, transformedClass);

            return transformedClass;
        }

        return super.findClass(name);
    }

    private Class<?> defineClass(String name, byte[] bytes) {
        return super.defineClass(name, bytes, 0, bytes.length);
    }

    public interface ITransformer {
        TransformerFlags transform(ClassNode classNode, Type classType);

        String getName();
    }

    public enum TransformerFlags {
        NO_REWRITE,
        SIMPLE_REWRITE,
        FULL_REWRITE;
    }
}