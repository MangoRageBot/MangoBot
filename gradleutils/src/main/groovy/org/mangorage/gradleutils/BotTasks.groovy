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

package org.mangorage.gradleutils

import com.mattmalec.pterodactyl4j.PteroBuilder
import com.mattmalec.pterodactyl4j.client.entities.PteroClient
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

class BotTasks {
    static final String group = "bot tasks";


    static void apply(Project project) {
        project.getTasks().register('runBot', JavaExec) {
            setGroup(group)
            setDescription("Runs the built in Launcher.")
            setDependsOn {
                List.of(project.getTasksByName("build", false)).iterator()
            }
            mustRunAfter(
                    project.getTasksByName("build", false)
            )

            setClasspath(project.sourceSets.main.runtimeClasspath)
            setMainClass('org.mangorage.mangobot.loader.Loader')
            setWorkingDir(project.file('build/run/'))
        }

        project.getTasks().register('publishAndRestartServer') {
            setGroup(group)
            setDescription("Publish and restart Bot server")

            setDependsOn {
                List.of(project.getTasksByName("publish", false)).iterator()
            }

            mustRunAfter(
                    project.getTasksByName("publish", false)
            )

            doLast {
                println("Restarting Server...")

                def secrets = new Properties()
                project.file("secrets.properties").withInputStream {
                    secrets.load(it)
                }

                PteroClient client = PteroBuilder.createClient("https://panel.sodiumhosting.com/", secrets.get("SERVER_TOKEN") as String);

                var server = client.retrieveServerByIdentifier("f32263f3").execute();
                if (server != null) {
                    if (server.isSuspended()) {
                        println("Server is suspended, unsuspending...");
                        server.start().execute();
                    } else {
                        server.restart().execute();
                        println("Restarted Discord Bot Server.");
                    }
                }
            }
        }
    }
}