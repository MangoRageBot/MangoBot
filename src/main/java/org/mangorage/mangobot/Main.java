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

import org.mangorage.mangobot.core.BotClassloader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Main {
    private static final String jarFile = ".jar";

    public static void main(String[] args) {
        try (BotClassloader botClassloader = new BotClassloader(Main.class.getClassLoader())) {

            Path libsDirectory = Path.of("libs/");
            Path pluginDirectory = Path.of("plugins/");

            try (Stream<Path> files = Files.walk(libsDirectory)) {
                files
                        .filter(path -> path.toFile().getName().endsWith(jarFile))
                        .forEach(path -> {
                            try {
                                botClassloader.addURL(path.toAbsolutePath().toUri().toURL());
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }

            try (Stream<Path> files = Files.walk(pluginDirectory)) {
                files
                        .filter(path -> path.toFile().getName().endsWith(jarFile))
                        .forEach(path -> {
                            try {
                                botClassloader.addURL(path.toAbsolutePath().toUri().toURL());
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }


            // List out urls

            URL[] urls = botClassloader.getURLs();

            // Print the URLs
            for (URL url : urls) {
                System.out.println("URL -> " + url.getFile());
            }


            try {
                Class<?> gson = botClassloader.loadClass("com.google.gson.Gson");
                System.out.println(gson);
                System.out.println(gson.newInstance());


                Class<?> mainClass = botClassloader.loadClass("org.mangorage.mangobot.BotMain");
                Method method = mainClass.getDeclaredMethod("main", String[].class);
                method.invoke(null, (Object) new String[]{});
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException exception) {
                throw new RuntimeException(exception);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
