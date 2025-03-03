/*
 * Copyright (c) 2023-2025. MangoRage
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

package org.mangorage.mangobot.loader;

import org.mangorage.mangobotapi.core.classloader.MangoClassloader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Loader {
    public static final List<URL> URLS;

    static {
        List<URL> urls = new ArrayList<>();
        List<Path> directories = List.of(Path.of("libraries/"), Path.of("plugins/"));

        directories.forEach(directory -> {
            try {
                addJars(urls, directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        URLS = urls;
    }

    private static void addJars(List<URL> urlList, Path directory) throws IOException {
        try (Stream<Path> files = Files.walk(directory)) {
            files
                    .filter(path -> path.toFile().getName().endsWith(".jar"))
                    .forEach(path -> {
                        try {
                            urlList.add(path.toAbsolutePath().toUri().toURL());
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    public static void next(String[] args, boolean useMangoloader) {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader().getParent();
        var urls = URLS.toArray(new URL[URLS.size()]);

        try (var classloader = useMangoloader ? new MangoClassloader(urls, oldCL) : new URLClassLoader(urls, oldCL)) {
            Thread.currentThread().setContextClassLoader(classloader);

            var clazz = useMangoloader ? "org.mangorage.mangobot.loader.CoreMain" : "org.mangorage.mangobot.loader.Loader";
            var methodName = useMangoloader ? "main" : "next";

            try {
                Class<?> mainClass = Class.forName(clazz, true, classloader);
                if (useMangoloader) {
                    Method method = mainClass.getDeclaredMethod(methodName, String[].class);
                    method.invoke(null, (Object) args); // Pass through the args...
                } else {
                    Method method = mainClass.getDeclaredMethod(methodName, String[].class, boolean.class);
                    method.invoke(null, (Object) args, true); // Pass through the args...
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException exception) {
                throw new RuntimeException(exception);
            } finally {
                System.err.println("Finished Running Launcher.");
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        next(args, false);
    }
}
