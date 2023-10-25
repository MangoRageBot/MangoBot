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
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.mangorage.basicutils.LogHelper;
import org.mangorage.mangobot.launcher.utils.Maven;
import org.mangorage.mangobot.launcher.utils.Util;
import org.mangorage.mangobot.launcher.utils.Version;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.jar.JarFile;

public class Installer {
    private static final Maven MAVEN = new Maven(
            "https://s01.oss.sonatype.org/content/repositories/releases",
            "io.github.realmangorage",
            "mangobot",
            "none", // default Version
            ".jar"
    );
    private static final Version VERSION = Util.getVersion();
    private static final String IVY_SETTINGS_PATH = "botresources/installerdata/ivysettings.xml";
    private static final String DEPS_JSON_PATH = "botresources/installerdata/dependencies.json";

    public static void main(String[] args) {
        String metadata = MAVEN.downloadMetadata();

        if (metadata == null) {
            LogHelper.info("Unable to download metadata...");
            return;
        }

        String latestVersion = Maven.parseLatestVersion(metadata);
        if (latestVersion == null) {
            LogHelper.info("Unable to parse latest version...");
            return;
        }


        if (VERSION != null) {
            LogHelper.info("Checking for Updates...");

            ComparableVersion oldVersion = new ComparableVersion(VERSION.version());
            ComparableVersion newVersion = new ComparableVersion(latestVersion);

            if (newVersion.compareTo(oldVersion) > 0) {
                LogHelper.info("Found latest Version: " + newVersion);
                LogHelper.info("Downloading...");
                downloadNewVersion(latestVersion);
                LogHelper.info("Downloaded!");
            } else {
                LogHelper.info("No new Version found!");
                downloadNewVersion(latestVersion);
            }
        } else {
            LogHelper.info("Installing Bot...");
            downloadNewVersion(latestVersion);
            LogHelper.info("Installed!");
        }
    }


    private static void downloadNewVersion(String version) {
        try {
            FileUtils.deleteDirectory(new File("libs"));
            FileUtils.deleteDirectory(new File("botresources/installer"));

            var jar = MAVEN.downloadTo(version, new File("libs/mangobot.jar"));

            try (JarFile jarFile = new JarFile(jar)) {
                FileUtils.copyInputStreamToFile(
                        jarFile.getInputStream(jarFile.getEntry(IVY_SETTINGS_PATH)),
                        new File("botresources/installer/ivysettings.xml")
                );

                FileUtils.copyInputStreamToFile(
                        jarFile.getInputStream(jarFile.getEntry(DEPS_JSON_PATH)),
                        new File("botresources/installer/dependencies.json")
                );

                File ivySettings = new File("botresources/installer/ivysettings.xml");
                File dependencies = new File("botresources/installer/dependencies.json");

                CoreInstaller.install(ivySettings, dependencies);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
