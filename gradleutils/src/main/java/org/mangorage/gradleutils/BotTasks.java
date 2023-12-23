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

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.JavaExec;

import java.util.ArrayList;

public class BotTasks {
    private static final String GROUP = "bot tasks";

    public static void apply(Project project, GradleUtilsPlugin gradleUtilsPlugin) {
        project.getTasks().register("runBot", JavaExec.class, task -> {
            task.setGroup(GROUP);
            task.setDescription("Runs the built in Launcher");

            ArrayList<Task> deps = new ArrayList<>();
            deps.addAll(project.getTasksByName("copyTask", false));
            deps.addAll(project.getTasksByName("runInstaller", false));

            task.setDependsOn(deps);
            task.mustRunAfter(deps);

            task.classpath(project.getConfigurations().getByName(gradleUtilsPlugin.getConfig().isPluginDevMode() ? "bot" : "botInternal").getFiles());
            task.setMain("org.mangorage.mangobot.loader.Loader");
            task.setWorkingDir(project.file("build/run/"));
        });

    }
}
