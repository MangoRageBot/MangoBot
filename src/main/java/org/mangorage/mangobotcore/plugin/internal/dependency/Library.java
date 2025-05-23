/*
 * Copyright (c) 2025. MangoRage
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

package org.mangorage.mangobotcore.plugin.internal.dependency;

import java.util.HashSet;
import java.util.Set;

public final class Library<T> implements Comparable<Library<?>> {
    private final T libraryObject;
    private final Set<String> dependencies = new HashSet<>();
    private int priority = 0;

    public Library(T value) {
        this.libraryObject = value;
    }

    public void addDependency(String id) {
        this.dependencies.add(id);
    }

    public void incrementPriority() {
        this.priority += 1;
    }

    public int getPriority() {
        return priority;
    }

    public T getObject() {
        return libraryObject;
    }

    @Override
    public int compareTo(Library<?> o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
