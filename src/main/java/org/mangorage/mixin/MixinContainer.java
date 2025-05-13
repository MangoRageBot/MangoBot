package org.mangorage.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.Mixins;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class MixinContainer extends ContainerHandleVirtual {
    /**
     * Creates a new root container handle.
     *
     * @param name the name
     * @since 1.0.0
     */
    public MixinContainer(final @NotNull String name) {
        super(name);
        Path path = Path.of("plugins/MangoBotCore-12.0.66.jar");

        addResource(path.getFileName().toString(), path);
    }

    /**
     * Adds a resource to this container.
     *
     * @param name the name
     * @param path the path
     * @since 1.0.0
     */
    public void addResource(final @NotNull String name, final @NotNull Path path) {
        this.add(new ResourceContainer(name, path));
    }

    /**
     * Adds a resource to this container.
     *
     * @param entry the entry
     * @since 1.0.0
     */
    public void addResource(final Map.@NotNull Entry<String, Path> entry) {
        this.add(new ResourceContainer(entry.getKey(), entry.getValue()));
    }

    @Override
    public String toString() {
        return "MixinContainer{name=" + this.getName() + "}";
    }

    /* package */ static class ResourceContainer extends ContainerHandleURI {
        private final String name;
        private final Path path;

        /* package */ ResourceContainer(final @NotNull String name, final @NotNull Path path) {
            super(path.toUri());

            this.name = name;
            this.path = path;
        }

        public @NotNull String name() {
            return this.name;
        }

        public @NotNull Path path() {
            return this.path;
        }

        @Override
        public @NotNull String toString() {
            return "ResourceContainer{name=" + this.name + ", path=" + this.path + "}";
        }
    }
}
