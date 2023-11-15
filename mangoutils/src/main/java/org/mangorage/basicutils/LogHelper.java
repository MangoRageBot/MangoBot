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

package org.mangorage.basicutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public final class LogHelper {
    public static final StackWalker WALKER = StackWalker.getInstance(Set.of(StackWalker.Option.values()));
    public static boolean writeToLogFile = true;

    public static void disableLogOutput() {
        writeToLogFile = false;
    }

    public static class LogFileHandler {
        private static LogFileHandler create(Path path) {
            return new LogFileHandler(path);
        }

        private final Path FILE_PATH;

        private LogFileHandler(Path path) {
            this.FILE_PATH = path;
            File file = FILE_PATH.toFile();

            try {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        throw new RuntimeException("File is a directory!");
                    }
                    Files.copy(file.toPath(), new File("logs/old_%s.log".formatted(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")))).toPath());

                    if (!file.delete() || !FILE_PATH.toFile().createNewFile()) {
                        throw new RuntimeException("Unable to create new file!");
                    }
                } else {
                	file.getParentFile().mkdirs();
                    if (!file.createNewFile())
                        throw new RuntimeException("Unable to create new file!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected void log(String content) {
            try {
                Files.writeString(
                        FILE_PATH,
                        content + "\n",
                        Files.exists(FILE_PATH) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static final LogFileHandler HANDLER = LogFileHandler.create(Path.of("logs/latest.log"));

    private static String getCallingClass() {
        return WALKER.getCallerClass().getCanonicalName();
    }

    private static String format(String type, String calledFrom, String content) {
        return "[%s] [%s] [%s/%s]: %s".formatted(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), calledFrom, Thread.currentThread().getName(), type, content);
    }

    private static String log(String logContent) {
        // Handle logs
        if (writeToLogFile)
            HANDLER.log(logContent);
        return logContent;
    }

    public static void info(String content) {
        System.out.println(log(format("INFO", WALKER.getCallerClass().getCanonicalName(), content)));
    }

    public static void error(String content) {
        System.err.println(log(format("ERROR", WALKER.getCallerClass().getCanonicalName(), content)));
    }

    public static void debug(String content) {
        System.out.println(log(format("DEBUG", WALKER.getCallerClass().getCanonicalName(), content)));
    }

    public static void warn(String content) {
        System.out.println(log(format("WARN", WALKER.getCallerClass().getCanonicalName(), "%s: %s".formatted("WARN", content))));
    }

    public static void fatal(String content) {
        System.err.println(log(format("FATAL", WALKER.getCallerClass().getCanonicalName(), content)));
    }

    public static void trace(String content) {
        System.out.println(log(format("TRACE", WALKER.getCallerClass().getCanonicalName(), content)));
    }
}
