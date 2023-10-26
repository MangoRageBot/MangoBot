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

package org.mangorage.mangobotapi.core.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mangorage.mangobotapi.core.util.APIUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DataHandler<T> {
    private static final Gson GSON_EXPOSE = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();
    private static final Gson GSON_NON_EXPOSE = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    public static List<File> getFiles(Path directory, Predicate<File> filePredicate, int depth) {
        try (Stream<Path> files = Files.walk(directory, depth, FileVisitOption.FOLLOW_LINKS)) {
            return files.map(Path::toFile).filter(File::isFile).filter(filePredicate).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDirectoryWithArgs(String path, String... args) {
        String result = path;
        for (String arg : args) {
            result = "%s/%s".formatted(result, arg);
        }

        return result;
    }


    /**
     * @param objectLoadingConsumer
     * @param type
     * @param directory
     * @param properties            -> properties for this DataHandler
     * @param <T>
     * @return
     */
    public static <T> DataHandler<T> create(Consumer<T> objectLoadingConsumer, Class<T> type, String directory, Properties properties) {
        return new DataHandler<>(objectLoadingConsumer, type, directory, properties);
    }

    public final static class Properties {
        public static Properties create() {
            return new Properties();
        }


        private boolean useExpose = false;
        private int depthLimit = 2; // default

        private String fileName;
        private Predicate<String> fileNamePredicate;

        private Properties() {
        }

        /**
         * Wether or not we should use -> {@link com.google.gson.annotations.Expose}
         *
         * @return
         */
        public Properties useExposeAnnotation() {
            this.useExpose = true;
            return this;
        }

        public Properties setDepthLimit(int depthLimit) {
            this.depthLimit = depthLimit;
            return this;
        }

        /**
         * Set to null if you want to use a dynamic fileName
         * you can pass thru your own fileName on save/load.
         * loadAll will use fileNamePredicate to determine
         * if it should load the file.
         */
        public Properties setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Properties setFileNamePredicate(Predicate<String> fileNamePredicate) {
            this.fileNamePredicate = fileNamePredicate;
            return this;
        }

        // Requires setFileName to be set
        public Properties useDefaultFileNamePredicate() {
            return setFileNamePredicate(e -> e.equals(getFileName()));
        }

        public String getFileName() {
            return fileName;
        }

        public Predicate<String> getFileNamePredicate() {
            return fileNamePredicate;
        }

        public boolean usesExposeAnnotation() {
            return useExpose;
        }

        public int getDepthLimit() {
            return depthLimit;
        }

        private Properties copy() {
            if (useExpose)
                return create().useExposeAnnotation().setDepthLimit(depthLimit).setFileName(fileName).setFileNamePredicate(fileNamePredicate);
            else
                return create().setDepthLimit(depthLimit).setFileName(fileName).setFileNamePredicate(fileNamePredicate);
        }
    }


    private final Consumer<T> objectLoadingConsumer;
    private final Class<T> type;
    private final String directory;
    private final Properties properties;

    private DataHandler(Consumer<T> objectLoadingConsumer, Class<T> type, String directory, Properties properties) {
        this.objectLoadingConsumer = objectLoadingConsumer;
        this.type = type;
        this.directory = directory;
        this.properties = properties.copy();
    }

    private Gson getGson() {
        return properties.usesExposeAnnotation() ? GSON_EXPOSE : GSON_NON_EXPOSE;
    }

    /**
     * @param object -> object to serialize
     * @param args   -> used if there is any %s in the directory
     */
    public void save(T object, String... args) {
        save(properties.getFileName(), object, args);
    }

    /**
     * @param object   -> object to serialize
     * @param fileName -> the name for the file. Includes extension
     * @param args     -> used if there is any %s in the directory
     */
    public void save(String fileName, T object, String... args) {
        // Make it so args can be construected and that we add /arg1/arg2/arg3 to the directory
        APIUtil.saveObjectToFile(getGson(), object, getDirectoryWithArgs(directory, args), fileName);
    }

    public void delete(String... args) {
        deleteFile(properties.getFileName(), args);
    }

    public void deleteFile(String fileName, String... args) {
        APIUtil.deleteFile(getDirectoryWithArgs(directory, args), fileName);
    }

    /**
     * Loads all objects in the directory
     */
    public void loadAll() {
        getFiles(Path.of(directory), (f) -> properties.getFileNamePredicate().test(f.getName()), properties.getDepthLimit()).forEach(
                (f) -> objectLoadingConsumer.accept(APIUtil.loadJsonToObject(getGson(), f.getAbsolutePath(), type))
        );
    }
}
