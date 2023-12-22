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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.mangorage.gradleutils.core.Constants;
import org.mangorage.gradleutils.core.TaskRegistry;
import org.mangorage.gradleutils.tasks.SetupInstallerTask;

import java.util.List;

public class GradleUtilsPlugin implements Plugin<Project> {
    private final Config config = new Config(this);
    private final TaskRegistry taskRegistry = new TaskRegistry(config);

    public TaskRegistry getTaskRegistry() {
        return taskRegistry;
    }

    public GradleUtilsPlugin() {
        taskRegistry.register(t -> {
            t.register("setupInstaller", SetupInstallerTask.class, Constants.INSTALLER_TASKS_GROUP);
            t.register("runInstaller", JavaExec.class, task -> {
                task.setGroup(Constants.INSTALLER_TASKS_GROUP);
                task.setDependsOn(List.of(task.getProject().getTasksByName("setupInstaller", false)));
                task.mustRunAfter(task.getProject().getTasksByName("setupInstaller", false));
                task.setWorkingDir(task.getProject().file("build/run/"));
                task.classpath(task.getProject().getConfigurations().getByName("installer").getFiles());
                task.setMain("org.mangorage.installer.Installer");
            });
        });
    }

    @Override
    public void apply(Project project) {
        project.getConfigurations().create("installer", t -> {
            t.setVisible(true);
        });
        project.getConfigurations().create("bot", t -> {
            t.setVisible(true);
            t.extendsFrom(project.getConfigurations().getByName("implementation"));
        });

        project.getExtensions().add("MangoBotConfig", config);


        project.afterEvaluate(a -> {
            taskRegistry.apply(project);

            DatagenTask.apply(project, this);
            BotTasks.apply(project, this);
        });
    }
}
