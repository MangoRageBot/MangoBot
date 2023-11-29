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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class MangoClassloader extends URLClassLoader {
    private Transformers transformers;

    public MangoClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void transform() {
        if (transformers != null) return;
        transformers = new Transformers(this);
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
                                transformers.add((ITransformer) transformerClass.getConstructor().newInstance());
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


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (transformers == null || transformers.empty())
            return super.findClass(name);

        if (transformers.containsClass(name))
            return transformers.getClazz(name);

        byte[] originalBytes = getClassBytes(name);

        if (originalBytes == null) {
            throw new ClassNotFoundException("Failed to load original class bytes for " + name);
        }

        byte[] arr = transformers.transform(name);
        if (arr != null) {
            Class<?> clz = defineClass(name, arr);
            transformers.add(name, clz);
            return clz;
        }

        return super.findClass(name);
    }

    private Class<?> defineClass(String name, byte[] bytes) {
        return super.defineClass(name, bytes, 0, bytes.length);
    }

    public enum TransformerFlags {
        NO_REWRITE,
        SIMPLE_REWRITE,
        FULL_REWRITE;
    }
}