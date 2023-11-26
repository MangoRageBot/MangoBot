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

package org.mangorage.mangobot;

import org.mangorage.mangobot.core.MangoClassloader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LauncherMain {
    private static final String jarFile = ".jar";
    public static URLClassLoader loader;

    private static void addJars(List<URL> urlList, Path directory) throws IOException {
        try (Stream<Path> files = Files.walk(directory)) {
            files
                    .filter(path -> path.toFile().getName().endsWith(jarFile))
                    .forEach(path -> {
                        try {
                            urlList.add(path.toAbsolutePath().toUri().toURL());
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-skipClassloading") || args[0].equalsIgnoreCase("-scl")) {
                String[] mainArgs = args.length == 1 ? new String[]{} : Arrays.copyOfRange(args, 1, args.length);
                Main.main(mainArgs);
                return;
            }
        }

        List<URL> urls = new ArrayList<>();
        List<Path> directories = List.of(Path.of("libs/"), Path.of("plugins/"));

        directories.forEach(directory -> {
            try {
                addJars(urls, directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ClassLoader oldCL = Thread.currentThread().getContextClassLoader().getParent();

        try (var classloader = new MangoClassloader(urls.toArray(new URL[urls.size()]), oldCL)) {
            Thread.currentThread().setContextClassLoader(classloader);
            loader = classloader;

            try {
                Class<?> mainClass = Class.forName("org.mangorage.mangobot.Main", true, classloader);
                Method method = mainClass.getDeclaredMethod("main", String[].class);
                method.invoke(null, (Object) args); // Pass through the args...
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
}