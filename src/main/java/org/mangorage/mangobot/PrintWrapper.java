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

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class PrintWrapper extends PrintStream {
    private static String getCallingClass() {
        var l = Thread.currentThread().getStackTrace();
        return l[3].getClassName();
    }

    private static String format(String type, String calledFrom, String content) {
        return "[%s] [%s] [%s/%s]: %s".formatted(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), calledFrom, Thread.currentThread().getName(), type, content);
    }

    public static void initWrapper() {
        System.setOut(new PrintWrapper(System.out, "INFO"));
        System.setErr(new PrintWrapper(System.err, "ERROR"));
    }

    private final String TYPE;

    private PrintWrapper(@NotNull OutputStream out, String type) {
        super(out);
        this.TYPE = type;
    }

    @Override
    public void println(String content) {
        super.println(format(TYPE, getCallingClass(), content));
    }
}
