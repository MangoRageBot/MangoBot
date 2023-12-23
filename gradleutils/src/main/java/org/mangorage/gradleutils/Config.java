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

package org.mangorage.gradleutils;

import org.gradle.api.Task;
import org.gradle.jvm.tasks.Jar;
import org.mangorage.gradleutils.core.Constants;
import org.mangorage.gradleutils.tasks.RestartServerTask;

import java.util.function.Supplier;

public class Config {
    private final GradleUtilsPlugin plugin;
    private boolean pluginDevMode = true;
    private Jar jarTask;
    private Supplier<Task> releaseTask = () -> null;

    public Config(GradleUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    public void enableRestartServerTask(String serverID, String serverURL, String serverToken, Task dependency) {
        plugin.getTaskRegistry().register(tasks -> {
            var clazz = dependency == null ? RestartServerTask.WithoutDep.class : RestartServerTask.class;
            if (dependency == null)
                tasks.register("restartServer", clazz, serverID, serverURL, serverToken, Constants.BOT_TASKS_GROUP);
            if (dependency != null)
                tasks.register("restartServer", clazz, serverID, serverURL, serverToken, Constants.BOT_TASKS_GROUP, dependency);
        });
    }

    public void setJarTask(Jar jar) {
        this.jarTask = jar;
    }

    public Jar getJarTask() {
        return jarTask;
    }


    public void disableCopyOverBot() {
        this.pluginDevMode = false;
    }

    public boolean isPluginDevMode() {
        return this.pluginDevMode;
    }

    public void setReleaseTask(Supplier<Task> task) {
        this.releaseTask = task;
    }

    public Task getReleaseTask() {
        return releaseTask.get();
    }
}
