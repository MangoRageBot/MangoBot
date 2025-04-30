package org.mangorage.mangobot.loader;

import java.io.File;
import java.io.IOException;
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

        // Cant have parent, for unkown reasons...
        // Needs to be null...
        URLClassLoader CL_libraries = new URLClassLoader(fetchJars(new File[]{new File("libraries")}), isDev ? null : Thread.currentThread().getContextClassLoader());

        try (URLClassLoader loader = new URLClassLoader(fetchJars(new File[]{new File("plugins")}), CL_libraries)) {
            Thread.currentThread().setContextClassLoader(loader);
            Class<?> clazz = Class.forName("org.mangorage.entrypoint.MangoBotCore", false, loader);
            System.out.println(clazz.getClassLoader());
            Method method = clazz.getDeclaredMethod("main", String[].class);
            method.invoke(null, (Object) args);
        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException("Failed to launch the application", e);
        }
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

        System.out.println("Found: " + urls.size());

        return urls.toArray(URL[]::new);
    }
}
