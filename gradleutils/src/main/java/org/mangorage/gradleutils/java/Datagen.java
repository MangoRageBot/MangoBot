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

package org.mangorage.gradleutils.java;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Datagen {


    private static final List<String> mavenRepositories = List.of(
            "https://repo.maven.apache.org/maven2/",
            "https://repo1.maven.org/maven2/"
            // Add additional Maven repositories as needed
            // "https://another.maven.repo/"
    );

    public static String generate(List<String> repos, String group, String name, String version) {
        for (String repository : repos) {
            String url = repository + group.replace('.', '/') + "/" + name + "/" + version + "/" + name + "-" + version + ".jar";
            //System.out.println(url);
            if (isValidUrl(url)) {
                return url;
            }
        }

        return null;
    }

    private static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }


    public static void getTransitiveDep(List<String> repos, ResolvedDependency dependency, Predicate<ModuleVersionIdentifier> checker, ArrayList<String> deps, ArrayList<ModuleVersionIdentifier> identifiers, ArrayList<String> urls) {
        var dep = dependency.getModule().getId();
        String id = dep.toString();

        if (!deps.contains(id) && checker.test(dep)) {
            deps.add(id);
            identifiers.add(dep);
            var result = generate(repos, dep.getGroup(), dep.getName(), dep.getVersion());
            if (result != null) {
                urls.add(result);
            }
        }

        identifiers.add(dependency.getModule().getId());

        dependency.getChildren().forEach(a -> getTransitiveDep(repos, a, checker, deps, identifiers, urls));
        if (dependency.getChildren().isEmpty()) {
            var a = 1;
        }
    }


    private static List<String> getAllRepositories(Project project) {
        List<String> repositories = new ArrayList<>(mavenRepositories);

        // Get repositories for regular project dependencies
        project.getRepositories().forEach(repo -> {
            if (repo instanceof MavenArtifactRepository && !isLocalRepository((MavenArtifactRepository) repo)) {
                repositories.add(((MavenArtifactRepository) repo).getUrl().toString());
            }
        });

        return repositories;
    }

    private static boolean isLocalRepository(MavenArtifactRepository repo) {
        // Check if the repository has a local repository layout
        return repo.getUrl().toString().startsWith("file:/");
    }

    public static void apply(Project project) {
        project.getTasks().register("runDatagen", task -> {
            task.setGroup("bot tasks");
            task.doLast(action -> {
                var repos = getAllRepositories(project);
                var deps = new ArrayList<String>();
                var idents = new ArrayList<ModuleVersionIdentifier>();
                var urls = new ArrayList<String>();

                var conf = project.getConfigurations().getByName("runtimeClasspath");

                conf.getResolvedConfiguration().getFirstLevelModuleDependencies().forEach(a -> getTransitiveDep(repos, a, (module) -> {
                    return !module.getGroup().contains("org.mangorage");
                }, deps, idents, urls));

                deps.forEach(System.out::println);
                System.out.printf("%s dependencies%n", deps.size());
                System.out.println("Direct URL's");
                //idents.forEach(System.out::println);
                //urls.forEach(System.out::println);

                var projectRootDir = project.getProjectDir().toPath();
                var depsFile = projectRootDir.resolve("src/main/resources/installerdata/deps.txt").toFile();


                try {
                    Files.deleteIfExists(depsFile.toPath());
                    Files.createDirectories(depsFile.toPath().getParent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(depsFile))) {
                    urls.forEach(a -> {
                        try {
                            writer.write(a + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    writer.close();
                    System.out.println("Dependencies have been generated to: " + depsFile);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });

        });
    }
}
