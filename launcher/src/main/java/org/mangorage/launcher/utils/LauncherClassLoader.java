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

package org.mangorage.launcher.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

public class LauncherClassLoader extends URLClassLoader {

    public LauncherClassLoader() {
        super(new URL[]{});
    }

    public void addURLs(URL[] urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

    public void addUrl(URL url) {
        super.addURL(url);
    }

    public void addJarsFromDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
            if (files != null) {
                for (File file : files) {
                    try {
                        URL jarURL = file.toURI().toURL();
                        addUrl(jarURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Invokes the application in this jar file given the name of the
     * main class and an array of arguments. The class must define a
     * static method "main" which takes an array of String arguemtns
     * and is of return type "void".
     *
     * @param name the name of the main class
     * @param args the arguments for the application
     * @throws ClassNotFoundException    if the specified class could not
     *                                   be found
     * @throws NoSuchMethodException     if the specified class does not
     *                                   contain a "main" method
     * @throws InvocationTargetException if the application raised an
     *                                   exception
     */
    public void invokeClass(String name, String[] args)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException {
        Class c = Class.forName(name, false, this);

        System.out.println(c.getName());
        Method m = c.getMethod("main", new Class[]{args.getClass()});
        m.setAccessible(true);
        int mods = m.getModifiers();
        if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
                !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("main");
        }
        try {
            m.invoke(null, new Object[]{args});
            System.out.println("FAILED");
        } catch (Exception e) {
            e.printStackTrace();
            // This should not happen, as we have disabled access checks
        }
    }

}
