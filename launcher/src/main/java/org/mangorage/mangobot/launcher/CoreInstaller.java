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

package org.mangorage.mangobot.launcher;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.LogOptions;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.mangorage.mangobot.launcher.utils.Dependency;
import org.mangorage.mangobot.launcher.utils.DependencyList;
import org.mangorage.mangobot.launcher.utils.Util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CoreInstaller {
    public record FileWithVersion(File file, String version) {
    }

    public static void install(File settingsFile, File dependencies) throws ParseException, IOException {
        Message.setDefaultLogger(new DefaultMessageLogger(Message.MSG_ERR));

        IvySettings settings = new IvySettings();
        settings.setVariable("custom.base.dir", new File("repo").getAbsolutePath());
        settings.load(settingsFile);

        Ivy ivy = Ivy.newInstance(settings);
        ivy.setSettings(settings);

        List<Dependency> deps = Util.loadJsonToObject(dependencies, DependencyList.class).libs();

        deps.forEach(dependency -> {
            ModuleRevisionId mrid = dependency.getMRI();

            // Create ResolveOptions and specify the configurations you want to resolve
            ResolveOptions resolveOptions = new ResolveOptions();
            resolveOptions.setConfs(new String[]{"default"});

            resolveOptions.setTransitive(true);
            resolveOptions.setDownload(true);
            resolveOptions.setRefresh(true);
            resolveOptions.setLog(LogOptions.LOG_QUIET);

            InstallOptions options = new InstallOptions();
            options.setConfs(new String[]{"default"});
            options.setOverwrite(true);

            try {
                ivy.install(mrid, "central", "custom", options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        HashMap<String, FileWithVersion> FILES = new HashMap<>();

        File dir = new File("repo");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                var nameArr = file.getName().replaceFirst(".jar", "").split("-");
                StringBuilder name = new StringBuilder();

                for (int i = 0; i < nameArr.length - 1; i++) {
                    if (i == (nameArr.length - 2))
                        name.append(nameArr[i]);
                    else
                        name.append(nameArr[i]).append("-");
                }

                if (FILES.containsKey(name.toString())) {
                    ComparableVersion old = new ComparableVersion(FILES.get(name.toString()).version());
                    ComparableVersion newV = new ComparableVersion(nameArr[nameArr.length - 1]);

                    if (newV.compareTo(old) > 0) {
                        FILES.put(name.toString(), new FileWithVersion(file, newV.toString()));
                    }
                } else {
                    FILES.put(name.toString(), new FileWithVersion(file, nameArr[nameArr.length - 1]));
                }
            }
        }


        FILES.forEach((key, fileWithVersion) -> {
            try {
                FileUtils.copyFile(fileWithVersion.file(), new File("libs/" + fileWithVersion.file().getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        FileUtils.deleteDirectory(dir);
    }
}
