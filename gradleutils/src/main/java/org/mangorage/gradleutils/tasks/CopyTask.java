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

package org.mangorage.gradleutils.tasks;

import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskAction;
import org.mangorage.gradleutils.Config;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

// PluginDev Task
public abstract class CopyTask extends Copy {
    private final boolean isPluginDev;

    @Inject
    public CopyTask(Config config) {
        this.isPluginDev = config.isPluginDevMode();
        var dependency = config.getJarTask();

        try {
            // Delete both if plugin mode otherwise just the one!
            Files.deleteIfExists(getProject().getProjectDir().toPath().resolve("build/run/plugins/%s".formatted("bot.jar")));
            if (config.isPluginDevMode())
                Files.deleteIfExists(getProject().getProjectDir().toPath().resolve("build/run/plugins/%s".formatted("plugin.jar")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        from(dependency.getArchiveFile().get().getAsFile());
        into(getProject().getProjectDir().toPath().resolve("build/run/plugins"));
        rename(a -> {
            return config.isPluginDevMode() ? "plugin.jar" : "bot.jar";
        });

        // Execute the Copy task after the Jar task
        dependsOn(dependency);
        mustRunAfter(dependency);
    }

    @TaskAction
    public void run() {
        if (isPluginDev) {
            getProject().getConfigurations().getByName("bot").getFiles().forEach(a -> {
                try {
                    Files.copy(a.toPath(), getProject().getProjectDir().toPath().resolve("build/run/plugins/bot.jar"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
