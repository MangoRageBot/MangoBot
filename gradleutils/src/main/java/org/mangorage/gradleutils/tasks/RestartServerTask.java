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

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.List;

public abstract class RestartServerTask extends DefaultTask {
    public static abstract class WithoutDep extends RestartServerTask {
        @Inject
        public WithoutDep(String serverID, String serverURL, String serverToken, String group) {
            super(serverID, serverURL, serverToken, group, null);
        }
    }

    private final String serverID;
    private final String serverURL;
    private final String serverToken;

    @Inject
    public RestartServerTask(String serverID, String serverURL, String serverToken, String group, Task dependency) {
        setGroup(group);
        setDescription("Restarts your server");
        this.serverID = serverID;
        this.serverURL = serverURL;
        this.serverToken = serverToken;

        if (dependency != null) {
            setDependsOn(List.of(dependency));
            mustRunAfter(dependency);
        }
    }

    @TaskAction
    public void run() {
        System.out.println("Restarting Server...");

        PteroClient client = PteroBuilder.createClient(serverURL, serverToken);

        var server = client.retrieveServerByIdentifier(serverID).execute();
        if (server != null) {
            if (server.isSuspended()) {
                System.out.println("Server is suspended, un-suspending...");
                server.start().execute();
            } else {
                server.restart().execute();
                System.out.println("Restarted Server.");
            }
        }
    }
}
