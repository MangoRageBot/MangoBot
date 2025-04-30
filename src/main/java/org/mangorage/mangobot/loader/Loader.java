package org.mangorage.mangobot.loader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Loader {
    public static void main(String[] args) {
        boolean isDev = false;

        for (String arg : args) {
            if (arg.contains("--dev"))
                isDev = true;
        }

        System.out.println("Path: " + Path.of("").toAbsolutePath());

        URLClassLoader CL_libraries = new URLClassLoader(fetchJars(new File[]{new File("libraries")}), isDev ? null : Thread.currentThread().getContextClassLoader().getParent());
        URLClassLoader cl = new URLClassLoader(fetchJars(new File[]{new File("plugins")}), CL_libraries);
        Thread.currentThread().setContextClassLoader(cl);
        callMain("org.mangorage.entrypoint.MangoBotCore", args, cl);
    }

    public static URL[] fetchJars(File[] directories) {
        // Add your extra folder here, you glutton for suffering

        List<URL> urls = new ArrayList<>();

        for (File dir : directories) {
            if (!dir.exists() || !dir.isDirectory()) continue;

            File[] jarFiles = dir.listFiles((d, name) -> name.endsWith(".jar"));
            if (jarFiles == null) continue;

            for (File jar : jarFiles) {
                try {
                    urls.add(jar.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Malformed URL while processing: " + jar.getAbsolutePath(), e);
                }
            }
        }

        return urls.toArray(URL[]::new);
    }

    public static void callMain(String className, String[] args, ClassLoader classLoader) {
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            Method mainMethod = clazz.getMethod("main", String[].class);

            // Make sure it's static and public
            if (!java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())) {
                throw new IllegalStateException("Main method is not static, are you high?");
            }

            // Invoke the main method with a godawful cast
            mainMethod.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't reflectively call main because something exploded.", e);
        }
    }
}