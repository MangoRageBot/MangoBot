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
import org.mangorage.gradleutils.Config;
import org.mangorage.gradleutils.core.Version;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;

public abstract class ReleaseTask extends DefaultTask {
    private final Version.Type type;
    @Inject
    public ReleaseTask(Config config, String group, Version.Type type) {
        this.type = type;
        var dep = config.getReleaseTask();
        setGroup(group);
        setFinalizedBy(List.of(dep));
    }

    @TaskAction
    public void run() {
        Version version = new Version(Path.of("version.txt"));
        switch (type) {
            case MAJOR -> version.bumpMajor();
            case MINOR -> version.bumpMinor();
            case PATCH -> version.bumpPatch();
        }
        getProject().setVersion(version.getValue());
    }
}
