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

package org.mangorage.mangobotcore.api.util.misc;

import java.util.function.Function;

// TODO: Make this better?
public final class Arguments {
    private static final Arguments EMPTY = new Arguments(new String[0]);

    public static Arguments empty() {
        return EMPTY;
    }

    public static Arguments of(String... args) {
        return new Arguments(args);
    }

    private final String[] args;

    private Arguments(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public String get(int index) {
        return index >= 0 && index < args.length ? args[index] : null;
    }

    public String getOrDefault(int index, String value) {
        return index >= 0 && index < args.length ? args[index] : value;
    }

    public boolean has(int index) {
        return index >= 0 && index < args.length;
    }

    public String getFrom(int index) {
        if (index < 0 || index >= args.length) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }

    public String findArg(String arg) {
        for (int i = 0, len = args.length - 1; i < len; i++) {
            if (arg.equals(args[i])) {
                return args[i + 1];
            }
        }
        return null;
    }

    public String findArgOrDefault(String arg, String defaultValue) {
        String result = findArg(arg);
        return result != null ? result : defaultValue;
    }

    public <X> X findArg(String arg, Function<String, X> resolver) {
        String value = findArg(arg);
        if (value == null) return null;

        try {
            return resolver.apply(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    public <X> X findArgOrDefault(String arg, Function<String, X> resolver, X defaultValue) {
        String value = findArg(arg);
        if (value == null) return defaultValue;

        try {
            X resolved = resolver.apply(value);
            return resolved != null ? resolved : defaultValue;
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public boolean hasArg(String arg) {
        for (String s : args) {
            if (arg.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public int getArgIndex(String arg) {
        for (int i = 0; i < args.length; i++) {
            if (arg.equals(args[i])) {
                return i;
            }
        }
        return -1;
    }
}

