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

package org.mangorage.mangobot.basicutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class LogHelper {

    private static final File LATEST_LOG;

    static {
        File dir = new File("logs/");
        if (!dir.exists())
            dir.mkdir();

        File latest_old = new File("logs/latest.log");

        if (latest_old.exists() && !latest_old.renameTo(new File("logs/old_%s.log".formatted(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))))))
            LogHelper.warn("Unable to rename latest.log to old_<date>.log");

        var a = 1;
        File latest_new = new File("logs/latest.log");
        if (!latest_new.exists()) {
            try {
                latest_new.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        LATEST_LOG = latest_new;
    }

    private static String getCallingClass() {
        var l = Thread.currentThread().getStackTrace();
        return l[3].getClassName();
    }

    private static String format(String type, String calledFrom, String content) {
        return "[%s] [%s] [%s/%s]: %s".formatted(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), calledFrom, Thread.currentThread().getName(), type, content);
    }

    //TODO: Remove this method, do better!
    @Deprecated
    private static String log(String logContent) {
        // Handle logs

        try {
            Files.writeString(
                    LATEST_LOG.toPath(),
                    logContent + "\n",
                    Files.exists(LATEST_LOG.toPath()) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return logContent;
    }

    public static void info(String content) {
        System.out.println(log(format("INFO", getCallingClass(), content)));
    }

    public static void error(String content) {
        System.err.println(log(format("ERROR", getCallingClass(), content)));
    }

    public static void debug(String content) {
        System.out.println(log(format("DEBUG", getCallingClass(), content)));
    }

    public static void warn(String content) {
        System.out.println(log(format("WARN", getCallingClass(), content)));
    }

    public static void fatal(String content) {
        System.err.println(log(format("FATAL", getCallingClass(), content)));
    }

    public static void trace(String content) {
        System.out.println(log(format("TRACE", getCallingClass(), content)));
    }
}
