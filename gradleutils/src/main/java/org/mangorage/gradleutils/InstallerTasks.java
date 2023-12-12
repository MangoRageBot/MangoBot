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
import org.gradle.api.tasks.JavaExec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class InstallerTasks {
    public static final String GROUP = "installer tasks";

    public static void apply(Project project) {
        var projectRootDir = project.getProjectDir().toPath();

        project.getTasks().register("setupInstaller", task -> {
            task.setGroup(GROUP);
            task.setDescription("Sets up the package.json for the installer");

            task.doLast(last -> {
                System.out.println("Copying over package.json");
                Path source = projectRootDir.resolve(Paths.get("installer/package.txt"));
                Path destination = projectRootDir.resolve(Paths.get("build/run/installer"));
                Path target = projectRootDir.resolve(Paths.get("build/run/installer/package.txt"));

                try {
                    Files.createDirectories(destination);
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File copied successfully from ${source.toAbsolutePath()} to ${destination.toAbsolutePath()}");
                } catch (IOException e) {
                    System.out.println("Error copying file: ${e.message}");
                    e.printStackTrace();
                }
            });
        });


        project.getTasks().register("runInstaller", JavaExec.class, task -> {
            task.setGroup(GROUP);
            task.setDependsOn(List.of(project.getTasksByName("setupInstaller", false)));
            task.mustRunAfter(project.getTasksByName("setupInstaller", false));

            task.setArgs(List.of("-launch"));
            task.setClasspath(project.files("installer/installer-1.1.0.jar"));
            task.setWorkingDir(project.file("build/run/"));
        });
    }
}
