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

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class SetupPluginsTask extends DefaultTask {

    public static void deleteFilesExceptJar(Path directoryPath) {
        File directory = directoryPath.toFile();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (!(file.getName().equals("bot.jar") || file.getName().equals("plugin.jar"))) {
                        file.delete();
                    }
                }
            }
        } else {
            System.out.println("Invalid directory path");
        }
    }

    @Inject
    public SetupPluginsTask() {
        setDescription("sets up the plugins");

        setDependsOn(List.of(getProject().getTasksByName("copyTask", false)));
        mustRunAfter(getProject().getTasksByName("copyTask", false));
    }

    @TaskAction
    public void run() {
        Path plugins = getProject().getRootDir().toPath().resolve("build/run/plugins");

        deleteFilesExceptJar(plugins);


        getProject().getConfigurations().getByName("plugin").getFiles().forEach(file -> {
            try {
                Files.copy(file.toPath(), plugins.resolve(file.getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
