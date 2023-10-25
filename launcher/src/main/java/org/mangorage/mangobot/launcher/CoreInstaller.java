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
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;

public class CoreInstaller {
    private static final List<ModuleRevisionId> DEPS = List.of(
            ModuleRevisionId.newInstance(
                    "net.dv8tion",
                    "JDA",
                    "5.0.0-beta.15"
            ),
            ModuleRevisionId.newInstance(
                    "dev.arbjerg",
                    "lavaplayer",
                    "2.0.2"
            ),
            ModuleRevisionId.newInstance(
                    "com.google.code.gson",
                    "gson",
                    "2.10.1"
            ),
            ModuleRevisionId.newInstance(
                    "org.apache.logging.log4j",
                    "log4j-core",
                    "2.20.0"
            ),
            ModuleRevisionId.newInstance(
                    "org.apache.logging.log4j",
                    "log4j-slf4j-impl",
                    "2.20.0"
            ),
            ModuleRevisionId.newInstance(
                    "org.eclipse.mylyn.github",
                    "org.eclipse.egit.github.core",
                    "2.1.5"
            ),
            ModuleRevisionId.newInstance(
                    "com.mattmalec",
                    "Pterodactyl4J",
                    "2.BETA_140"
            ),
            ModuleRevisionId.newInstance(
                    "org.slf4j",
                    "slf4j-simple",
                    "2.0.9"
            ),
            ModuleRevisionId.newInstance(
                    "org.reflections",
                    "reflections",
                    "0.10.2"
            )
    );

    public static void main(String[] args) throws ParseException, IOException {
        install();
    }

    public static void install() throws ParseException, IOException {

        var url = CoreInstaller.class.getResource("/ivysettings.xml");

        FileUtils.copyURLToFile(url, FileUtils.getFile("ivysettings.xml"));

        IvySettings settings = new IvySettings();
        settings.setVariable("custom.base.dir", new File("repo").getAbsolutePath());
        settings.load(url);

        Ivy ivy = Ivy.newInstance(settings);
        ivy.setSettings(settings);


        DEPS.forEach(mrid -> {
            // Create ResolveOptions and specify the configurations you want to resolve
            ResolveOptions resolveOptions = new ResolveOptions();
            resolveOptions.setConfs(new String[]{"default"});

            resolveOptions.setTransitive(true);
            resolveOptions.setDownload(true);
            resolveOptions.setRefresh(true);


            InstallOptions options = new InstallOptions();
            options.setConfs(new String[]{"default"});
            options.setOverwrite(true);

            try {
                ivy.install(mrid, "central", "custom", options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        HashSet<String> files = new HashSet<>();


        File dir = new File("repo");
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                var nameArr = file.getName().replaceFirst(".jar", "").split("-");
                var name = "";
                for (int i = 0; i < nameArr.length - 1; i++) {
                    name += nameArr[i] + "-";
                }
                if (files.contains(name)) {
                    System.out.println("Found duplicate jar");
                } else {
                    files.add(name);
                    FileUtils.copyFile(file, new File("libs/" + file.getName()));
                }
            }
        }

        FileUtils.deleteDirectory(dir);
    }
}
