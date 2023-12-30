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

package org.mangorage.basicutils.misc;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.function.Consumer;

public class FileMonitor extends Thread {

    private static final FileMonitor MONITOR;

    static {
        MONITOR = new FileMonitor();
        MONITOR.start();
    }

    public static FileMonitor getMonitor() {
        return MONITOR;
    }

    private final WatchService service;
    private final HashMap<Path, Consumer<WatchEvent.Kind<?>>> REGISTERED = new HashMap<>();

    private FileMonitor() {
        try {
            service = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Path path, Consumer<WatchEvent.Kind<?>> consumer, WatchEvent.Kind<?>... kinds) {
        Path parentDirectory = path.toAbsolutePath().getParent();

        if (parentDirectory != null && Files.isDirectory(parentDirectory)) {
            try {
                REGISTERED.put(path.toAbsolutePath(), consumer);
                parentDirectory.register(service, kinds);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("Cannot register file for monitoring without a valid parent directory: " + path);
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            while (true) {
                WatchKey key = service.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue; // Ignore overflow events
                    }

                    Path relativePath = (Path) event.context();
                    Path modifiedPath = (Path) key.watchable();

                    // Append the relative path to the base path
                    modifiedPath = modifiedPath.resolve(relativePath);

                    // Check if any registered path equals the modified path
                    for (Path registeredPath : REGISTERED.keySet()) {
                        if (registeredPath.equals(modifiedPath.toAbsolutePath())) {
                            REGISTERED.get(registeredPath).accept(kind);
                            break; // Break once a match is found
                        }
                    }
                }

                key.reset();
            }

        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Close the WatchService when no longer needed.
     */
    public void close() {
        try {
            service.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
