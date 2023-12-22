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
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.JavaExec;
import org.gradle.jvm.tasks.Jar;

import java.util.List;

public class BotTasks {
    private static final String GROUP = "bot tasks";

    public static void apply(Project project, GradleUtilsPlugin gradleUtilsPlugin) {

        Jar jarTask = (Jar) project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME);
        project.getTasks().register("copyTask", Copy.class, copyTask -> {
            copyTask.from(jarTask.getArchiveFile().get().getAsFile());
            copyTask.into(project.getRootProject().getProjectDir().toPath().resolve("build/run/plugins"));
            copyTask.rename(a -> {
                return "plugin.jar";
            });

            // Execute the Copy task after the Jar task
            copyTask.dependsOn(jarTask);
            copyTask.mustRunAfter(jarTask);
        });


        project.getTasks().register("runBot", JavaExec.class, task -> {
            task.setGroup(GROUP);
            task.setDescription("Runs the built in Launcher");
            task.setDependsOn(List.of(project.getTasksByName("copyTask", false)));
            task.mustRunAfter(project.getTasksByName("copyTask", false));

            task.classpath(project.getConfigurations().getByName("bot").getFiles());
            task.setMain("org.mangorage.mangobot.loader.Loader");
            task.setWorkingDir(project.file("build/run/"));
        });

    }
}
