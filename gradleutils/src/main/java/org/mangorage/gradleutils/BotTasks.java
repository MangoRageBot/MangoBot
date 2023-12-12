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

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class BotTasks {
    private static final String GROUP = "bot tasks";

    public static void apply(Project project) {

        project.getTasks().register("runBot", JavaExec.class, task -> {
            task.setGroup(GROUP);
            task.setDescription("Runs the built in Launcher");
            task.setDependsOn(List.of(project.getTasksByName("build", false)));
            task.mustRunAfter(project.getTasksByName("build", false));

            //task.setClasspath(project.getExtensions().getByType(SourceSet.class).getRuntimeClasspath());
            task.setWorkingDir(project.file("build/run/"));
        });

        project.getTasks().register("publishAndRestartServer", task -> {
            task.setGroup(GROUP);
            task.setDescription("Publish and restart Bot Server");
            task.setDependsOn(List.of(project.getRootProject().getTasksByName("publish", false)));
            task.mustRunAfter(project.getRootProject().getTasksByName("publish", false));

            task.doLast(last -> {
                System.out.println("Restarting Server...");

                var secrets = new Properties();
                try {
                    var is = project.file("secrets.properties").toURI().toURL().openStream();
                    secrets.load(is);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                PteroClient client = PteroBuilder.createClient("https://panel.sodiumhosting.com/", (String) secrets.get("SERVER_TOKEN"));

                var server = client.retrieveServerByIdentifier("f32263f3").execute();
                if (server != null) {
                    if (server.isSuspended()) {
                        System.out.println("Server is suspended, unsuspending...");
                        server.start().execute();
                    } else {
                        server.restart().execute();
                        System.out.println("Restarted Discord Bot Server.");
                    }
                }
            });
        });


    }
}
