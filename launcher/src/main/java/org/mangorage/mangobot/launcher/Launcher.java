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
import org.mangorage.mangobot.launcher.data.Maven;
import org.mangorage.mangobot.launcher.data.Version;
import org.mangorage.mangobot.launcher.utils.Util;
import org.mangorage.mangobot.launcher.utils.Versions;

import java.io.File;
import java.util.List;

public class Launcher {

    public static final List<Maven> DEPENDENCIES = List.of(
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "net.dv8tion",
                    "JDA",
                    Versions.JDA_VERSION, // default Version
                    ".jar"
            ),
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "dev.arbjerg",
                    "lavaplayer",
                    Versions.LAVA_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "com.google.code.gson",
                    "gson",
                    Versions.GSON_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "org.apache.logging.log4j",
                    "log4j-core",
                    Versions.LOG4J_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "org.eclipse.mylyn.github",
                    "org.eclipse.egit.github.core",
                    "2.1.5",
                    ".jar"
            ),
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "org.slf4j",
                    "slf4j-simple",
                    "2.0.9",
                    ".jar"
            ),
            new Maven(
                    "https://repo.maven.apache.org/maven2",
                    "dev.arbjerg",
                    "lava-common",
                    Versions.LAVA_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "dev.arbjerg",
                    "lavaplayer-natives",
                    Versions.LAVA_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.slf4j",
                    "slf4j-api",
                    "1.7.36",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.apache.logging.log4j",
                    "log4j-api",
                    Versions.LOG4J_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "commons-io",
                    "commons-io",
                    "2.14.0",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "net.sf.trove4j",
                    "trove4j",
                    "3.0.3",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.apache.commons",
                    "commons-collections4",
                    "4.4",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "com.squareup.okhttp3",
                    "okhttp",
                    "4.10.0",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.jetbrains.kotlin",
                    "kotlin-stdlib",
                    "1.6.20",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "com.squareup.okio",
                    "okio-jvm",
                    "3.0.0",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "com.neovisionaries",
                    "nv-websocket-client",
                    "2.14",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "com.fasterxml.jackson.core",
                    "jackson-core",
                    "2.15.2",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "com.fasterxml.jackson.core",
                    "jackson-databind",
                    "2.15.2",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "com.fasterxml.jackson.core",
                    "jackson-annotations",
                    "2.15.2",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.apache.httpcomponents",
                    "httpclient",
                    "4.5.14",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.apache.httpcomponents",
                    "httpcore",
                    "4.4.16",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.mozilla",
                    "rhino",
                    "1.7.14",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.mozilla",
                    "rhino-engine",
                    "1.7.14",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.slf4j",
                    "slf4j-simple",
                    "2.0.9",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.apache.logging.log4j",
                    "log4j-slf4j-impl",
                    Versions.LOG4J_VERSION,
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "commons-logging",
                    "commons-logging",
                    "1.2",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.json",
                    "json",
                    "20230618",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.reflections",
                    "reflections",
                    "0.10.2",
                    ".jar"
            ),
            new Maven(
                    "https://repo1.maven.org/maven2",
                    "org.javassist",
                    "javassist",
                    "3.28.0-GA",
                    ".jar"
            )
    );

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

        var version = Util.getVersion();
        if (version != null) {
            //startBot(version.version());
        } else {
            LogHelper.info("Unable to find Bot jar...");
        }
    }


    public static void downloadDependencies() {
        File dir = new File("libs/");
        if (!dir.exists() && !dir.mkdirs()) {
            LogHelper.info("Unable to create directories for dependencies...");
            return;
        }
        DEPENDENCIES.forEach(e -> {
            e.downloadTo(e.version(), new File("libs/%s-%s.jar".formatted(e.artifactId(), e.version())));
        });
    }
}
