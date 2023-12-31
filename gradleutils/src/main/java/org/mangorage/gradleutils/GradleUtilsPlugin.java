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
import org.mangorage.gradleutils.core.Constants;
import org.mangorage.gradleutils.core.TaskRegistry;
import org.mangorage.gradleutils.core.Version;
import org.mangorage.gradleutils.tasks.CopyTask;
import org.mangorage.gradleutils.tasks.DatagenTask;
import org.mangorage.gradleutils.tasks.ReleaseTask;
import org.mangorage.gradleutils.tasks.RunBotTask;
import org.mangorage.gradleutils.tasks.RunInstallerTask;
import org.mangorage.gradleutils.tasks.SetupPluginsTask;

import java.util.Objects;

public class GradleUtilsPlugin implements Plugin<Project> {
    private final Config config = new Config(this);
    private final TaskRegistry taskRegistry = new TaskRegistry(config);

    public TaskRegistry getTaskRegistry() {
        return taskRegistry;
    }

    public Config getConfig() {
        return config;
    }

    public GradleUtilsPlugin() {
        taskRegistry.register(t -> {
            t.register("copyTask", CopyTask.class, config);
            t.register("runBot", RunBotTask.class, config, Constants.BOT_TASKS_GROUP);
            t.register("runInstaller", RunInstallerTask.class, Constants.INSTALLER_TASKS_GROUP);
            t.register("setupPlugins", SetupPluginsTask.class);
            if (config.getReleaseTask() != null) {
                t.register("releaseMajor", ReleaseTask.class, config, Constants.BOT_TASKS_GROUP, Version.Type.MAJOR);
                t.register("releaseMinor", ReleaseTask.class, config, Constants.BOT_TASKS_GROUP, Version.Type.MINOR);
                t.register("releasePatch", ReleaseTask.class, config, Constants.BOT_TASKS_GROUP, Version.Type.PATCH);
            }
        });
    }

    @Override
    public void apply(Project project) {
        project.getExtensions().add("MangoBotConfig", config);

        project.getConfigurations().create("installer", t -> {
            t.setVisible(true);
        });

        var botCfg = project.getConfigurations().create("bot", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        project.getConfigurations().create("botInternal", t -> {
            t.setVisible(true);
            t.setTransitive(false);
            t.extendsFrom(project.getConfigurations().getByName("implementation"));
        });

        var plugin = project.getConfigurations().create("plugin", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        var library = project.getConfigurations().create("library", t -> {
            t.setVisible(true);
            t.setTransitive(true);
            t.setCanBeResolved(true);
        });

        var embededLibrary = project.getConfigurations().create("embedLibrary", t -> {
            t.setVisible(true);
        });

        project.getConfigurations().findByName("implementation").extendsFrom(botCfg, plugin, library, embededLibrary);

        project.afterEvaluate(a -> {
            Objects.requireNonNull(config.getJarTask(), "jarTask cannot be null!");
            taskRegistry.apply(project);
            DatagenTask.apply(project, this);
        });
    }
}
