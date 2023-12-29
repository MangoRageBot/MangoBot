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

package org.mangorage.gradleutils.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class Version {
    public enum Type {
        MAJOR,
        MINOR,
        PATCH
    }

    public static Integer[] readFromFile(Path file) {
        Integer[] result = new Integer[3];

        if (!file.toFile().exists()) writeToFile(file, "1.0.0");

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split("\\.");
                for (int i = 0; i < 3; i++) {
                    result[i] = Integer.parseInt(parts[i]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(); // Handle exceptions appropriately in your application
        }

        return result;
    }

    public static void writeToFile(Path fileName, String version) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName.toFile()))) {
            writer.write(version);
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately in your application
        }
    }

    private int major = 1;
    private int minor = 0;
    private int patch = 0;
    private final Path versionFile;

    public Version(Path versionFile) {
        this.versionFile = versionFile;
        Integer[] version = readFromFile(versionFile);

        if (version.length == 3) {
            this.major = version[0];
            this.minor = version[1];
            this.patch = version[2];
        }
    }

    public static void main(String[] args) {
        Version version = new Version(Path.of("version.txt"));
        version.bumpPatch();
        System.out.println(version.getValue());
    }

    public void bumpMajor() {
        major++;
        this.minor = 0;
        this.patch = 0;
        save();
    }

    public void bumpMinor() {
        minor++;
        this.patch = 0;
        save();
    }

    public void bumpPatch() {
        patch++;
        save();
    }

    public void save() {
        writeToFile(versionFile, getValue());
    }

    public String getValue() {
        return "%s.%s.%s".formatted(major, minor, patch);
    }
}
