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

package org.mangorage.mangobotcore.internal.plugin.dependency;

import org.mangorage.mangobotcore.api.plugin.v1.Dependency;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public final class LibraryManager<T> {
    private final HashMap<String, Library<T>> libraries = new HashMap<>();

    public void addLibrary(String id, T value) {
        libraries.put(id, new Library<>(value));
    }

    public void addDependenciesForLibrary(String id, List<? extends Dependency> dependencies) {
        var library = libraries.get(id);
        if (library != null) {
            for (var dependency : dependencies) {
                var dependencyLibrary = libraries.get(dependency.getId());
                if (dependencyLibrary == null) {
                    switch (dependency.getType()) {
                        case REQUIRED ->
                                throw new IllegalStateException("Failed to find Dependency %s for %s".formatted(dependency.getId(), id));
                    }
                } else {
                    dependencyLibrary.incrementPriority();
                    library.addDependency(dependency.getId());
                }
            }
        }
    }

    public Collection<Library<T>> getLibraries() {
        return libraries.values();
    }

    public List<Library<T>> getLibrariesInOrder() {
        return libraries.values().stream()
                .sorted()
                .toList();
    }
}
