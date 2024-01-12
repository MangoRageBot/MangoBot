/*
 * Copyright (c) 2023-2024. MangoRage
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

package org.mangorage.mangobot.test.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mangorage.basicutils.config.Config;
import org.mangorage.basicutils.config.ConfigSetting;
import org.mangorage.basicutils.config.ISetting;
import org.mangorage.basicutils.config.Transformers;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {
    @Test
    @DisplayName("Config Test")
    public void testConfigMain() {
        Config config = new Config(Path.of("build/tests/test.conf"));
        config.getFile().toFile().deleteOnExit();

        ISetting<Integer> data = ConfigSetting.create(
                config,
                "test",
                Transformers.INTEGER,
                100
        );
        data.set(100);
        assertEquals(100, data.get());
        data.set(10);

        Config newConfig = new Config(config.getFile());
        ISetting<Integer> newData = ConfigSetting.create(
                config,
                "test",
                Transformers.INTEGER,
                100
        );
        assertEquals(10, newData.get());
    }
}
