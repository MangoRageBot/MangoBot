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

import org.gradle.api.Task;
import org.gradle.api.tasks.JavaExec;
import org.mangorage.gradleutils.Config;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class RunBotTask extends JavaExec {
    @Inject
    public RunBotTask(Config config, String group) {
        setGroup(group);
        setDescription("Runs the bot");

        ArrayList<Task> deps = new ArrayList<>();
        deps.addAll(getProject().getTasksByName("copyTask", false));
        deps.addAll(getProject().getTasksByName("runInstaller", false));

        setDependsOn(deps);
        mustRunAfter(deps);

        classpath(getProject().getConfigurations().getByName(config.isPluginDevMode() ? "bot" : "botInternal").getFiles());
        setMain("org.mangorage.mangobot.loader.Loader");
        setWorkingDir(getProject().file("build/run/"));
    }
}
