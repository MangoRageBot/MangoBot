package org.mangorage.mangobotcore.api.plugin.v1;

import java.util.Optional;

public interface Plugin {
    String getId();
    void load();

    /**
     * Allows for someone to request an Object from a Plugin with a given id
     */
    default <T> Optional<T> requestObject(Class<T> tClass, String id) {
        return Optional.empty();
    }
}
