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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class SetupInstallerTask extends DefaultTask {

    @Inject
    public SetupInstallerTask(String group) {
        setGroup(group);
        setDescription("Restarts your server");
    }

    @TaskAction
    public void run() {
        var projectRootDir = getProject().getProjectDir().toPath();

        System.out.println("Copying over package.json");
        Path source = projectRootDir.resolve(Paths.get("installer/package.txt"));
        Path destination = projectRootDir.resolve(Paths.get("build/run/installer"));
        Path target = projectRootDir.resolve(Paths.get("build/run/installer/package.txt"));

        try {
            Files.createDirectories(destination);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully from %s to %s".formatted(source, destination));
        } catch (IOException e) {
            System.out.println("Error copying file: %s".formatted(e.getMessage()));
            e.printStackTrace();
        }
    }
}
