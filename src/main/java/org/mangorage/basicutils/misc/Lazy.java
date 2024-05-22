/*
 * Copyright (c) 2024. MangoRage
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

import java.util.function.Supplier;

public interface Lazy<T> extends Supplier<T> {
    void invalidate();

    /**
     * Constructs a lazy-initialized object
     *
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Fast<T>(supplier);
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     *
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> Lazy<T> concurrentOf(Supplier<T> supplier) {
        return new Concurrent<T>(supplier);
    }

    /**
     * Non-thread-safe implementation.
     */
    final class Fast<T> implements Lazy<T> {
        private final Supplier<T> supplier;
        private T instance;

        private Fast(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public final T get() {
            if (instance == null) {
                instance = supplier.get();
            }
            return instance;
        }

        @Override
        public void invalidate() {
            this.instance = null;
        }
    }

    /**
     * Thread-safe implementation.
     */
    final class Concurrent<T> implements Lazy<T> {
        private final Object lock = new Object();
        private final Supplier<T> supplier;
        private volatile T instance;

        private Concurrent(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public final T get() {
            var ret = instance;
            if (ret == null) {
                synchronized (lock) {
                    if (instance == null) {
                        return instance = supplier.get();
                    }
                }
            }
            return ret;
        }

        @Override
        public void invalidate() {
            synchronized (lock) {
                this.instance = null;
            }
        }
    }
}
