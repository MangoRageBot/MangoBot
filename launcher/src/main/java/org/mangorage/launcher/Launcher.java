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

package org.mangorage.launcher;

import org.mangorage.launcher.utils.LauncherClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {

        if (args.length < 1) {
            throw new IllegalStateException("need more args...");
        }

        String mainClass = args[0];
        ArrayList<File> directorys = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            directorys.add(new File(args[i]));
        }

        System.out.println("Main class: " + mainClass);
        for (File directory : directorys) {
            System.out.println("Loaded Directory: " + directory.getAbsolutePath());
        }


        LauncherClassLoader classLoader = new LauncherClassLoader();
        for (File directory : directorys) {
            classLoader.addJarsFromDirectory(directory);
        }

        try {
            classLoader.invokeClass("org.mangorage.mangobot.Main", new String[]{});
        } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
