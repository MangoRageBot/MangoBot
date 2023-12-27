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

import org.gradle.api.tasks.JavaExec;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

public abstract class RunInstallerTask extends JavaExec {
    @Inject
    public RunInstallerTask(String group) {
        setGroup(group);
        setDependsOn(List.of(getProject().getTasksByName("setupPlugins", false)));
        mustRunAfter(getProject().getTasksByName("setupPlugins", false));

        setWorkingDir(getProject().file("build/run/"));
        classpath(getProject().getConfigurations().getByName("installer").getFiles());
        setMain("org.mangorage.installer.Installer");
    }

    /**
     * @return
     */
    @Override
    public List<String> getArgs() {
        Path plugins = getProject().getRootDir().toPath().resolve("build/run/plugins");
        StringBuilder builder = new StringBuilder();

        for (File file : plugins.toFile().listFiles()) {
            System.out.println(file.getName());
            if (!file.isDirectory() && file.getName().contains(".jar"))
                builder.append(file.toPath().toAbsolutePath()).append(";");
        }

        String args = builder.substring(0, builder.length() - 1);

        setArgs(List.of("-manualJar", args));
        return super.getArgs();
    }
}
