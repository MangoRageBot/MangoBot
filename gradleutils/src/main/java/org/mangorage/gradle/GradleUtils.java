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

package org.mangorage.gradle;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;

public class GradleUtils {
    public static void main(String[] args) {
        String projectDir = "F:\\Discord Bot Projects\\mangobot";

        // Create a connector
        GradleConnector connector = GradleConnector.newConnector();

        // Set the project directory
        connector.forProjectDirectory(new File(projectDir));

        // Establish a connection to the Tooling API
        try (ProjectConnection connection = connector.connect()) {
            // Use the Tooling API here
            // For example, you can execute tasks, get the model, etc.
            // Configure and execute a custom task
            // Create a BuildLauncher
            BuildLauncher buildLauncher = connection.newBuild();

            // Specify the tasks to be executed as arguments
            buildLauncher.forTasks("myTask");

            // Run the build
            buildLauncher.run();
        }
    }
}
