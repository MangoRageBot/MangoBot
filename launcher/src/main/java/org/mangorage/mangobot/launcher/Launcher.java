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

import org.mangorage.basicutils.LogHelper;
import org.mangorage.mangobot.launcher.utils.Maven;
import org.mangorage.mangobot.launcher.utils.Util;
import org.mangorage.mangobot.launcher.utils.Version;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Launcher {
    public static final Maven MAVEN = new Maven(
            "https://s01.oss.sonatype.org/content/repositories/releases",
            "io.github.realmangorage",
            "mangobot",
            "none", // default Version
            ".jar"
    );

    public static void main(String[] args) {
        LogHelper.info("Checking for Updates...");

        File dest = new File("libs/mangobot.jar");
        if (!dest.exists())
            downloadDependencies(); // Download the deps...
        File parent = dest.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            LogHelper.info("Unable to create directories for bot jar...");
            return;
        }

        String metadata = MAVEN.downloadMetadata();
        if (metadata != null) {
            String version = Maven.parseLatestVersion(metadata);

            if (version != null) {
                LogHelper.info("Found latest Version: " + version);
                // Handle check for updates...
                Version currentVersion = Util.getVersion();
                if (currentVersion == null) {
                    LogHelper.info("No current version found, downloading latest version...");
                    MAVEN.downloadTo(version, dest);
                    Util.saveVersion(version);
                } else {
                    if (currentVersion.version().equals(version)) {
                        LogHelper.info("No updates found, starting bot...");
                    } else {
                        LogHelper.info("Found new version, downloading...");
                        MAVEN.downloadTo(version, dest);
                        Util.saveVersion(version);
                    }
                }
            }
        }
    }

    public static void downloadDependencies() {
        try {
            CoreInstaller.install();
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
