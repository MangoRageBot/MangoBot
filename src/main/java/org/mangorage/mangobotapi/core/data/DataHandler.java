/*
 * Copyright (c) 2024-2025. MangoRage
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataHandler<T extends IFileNameResolver> {
    private static final Gson DEFAULT_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Builder create() {
        return new Builder();
    }

    private static void save(String content, Path path) throws IOException {
        if (!Files.exists(path.getParent()))
            Files.createDirectories(path.getParent());

        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private final Class<T> tClass;
    private final Gson gson;
    private final String path;
    private final boolean isFile;
    private final int maxDepth;


    private DataHandler(Class<T> tClass, Gson gson, String path, boolean isFile, int maxDepth) {
        this.tClass = tClass;
        this.gson = gson;
        this.path = !isFile ? path : path.endsWith(".json") ? path : path + ".json";
        this.isFile = isFile;
        this.maxDepth = maxDepth;
    }

    public void save(Path rootDirectory, T... objects) {
        if (objects.length == 0) return;
        Path resolved = rootDirectory.resolve(path).toAbsolutePath();

        if (isFile) {
            String data = gson.toJson(objects[0]);
            try {
                save(data, resolved);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            for (T object : objects) {
                String data = gson.toJson(object);
                try {
                    var fn = object.resolve();
                    save(data, resolved.resolve(fn.path()).resolve(fn.name() + ".json").toAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void delete(Path rootDirectory, T... objects) {
        if (objects.length == 0) return;
        Path resolved = rootDirectory.resolve(path).toAbsolutePath();

        if (isFile) {
            try {
                Files.deleteIfExists(resolved);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            for (T object : objects) {
                var fn = object.resolve();
                var filePath = resolved.resolve(fn.path()).resolve(fn.name() + ".json").toAbsolutePath();
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public List<T> load(Path rootDirectory) {
        ArrayList<T> list = new ArrayList<>();
        Path resolved = rootDirectory.resolve(path).toAbsolutePath();
        if (Files.exists(resolved)) {
            if (!isFile) {
                APIUtil.scanDirectory(resolved.toFile(), maxDepth)
                        .stream()
                        .map(Path::toFile)
                        .filter(file -> file.isFile() && !file.isDirectory())
                        .forEach(f -> {
                            try {
                                T obj = gson.fromJson(new FileReader(f), tClass);
                                list.add(obj);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
        return list;
    }

    public Optional<T> loadFile(Path rootDirectory) {
        if (isFile) {
            Path resolved = rootDirectory.resolve(path).toAbsolutePath();
            if (Files.exists(resolved)) {
                try {
                    T obj = gson.fromJson(new FileReader(resolved.toFile()), tClass);
                    return Optional.of(obj);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Optional.empty();
    }


    public static class Builder {
        private boolean isFile = false;
        private String path = "";
        private int maxDepth = 0;
        private Gson gson = DEFAULT_GSON;

        public Builder file() {
            isFile = true;
            return this;
        }

        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder maxDepth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        public <T extends IFileNameResolver> DataHandler<T> build(Class<T> tClass) {
            return new DataHandler<>(tClass, gson, path, isFile, maxDepth);
        }
    }
}
