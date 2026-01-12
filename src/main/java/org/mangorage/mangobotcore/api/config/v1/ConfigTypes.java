package org.mangorage.mangobotcore.api.config.v1;

public interface ConfigTypes {
    IConfigType<String> STRING = IConfigType.create(s -> s, s -> s);
    IConfigType<Boolean> BOOLEAN = IConfigType.create(Boolean::parseBoolean, Object::toString);
    IConfigType<Integer> INTEGER = IConfigType.create(Integer::parseInt, Object::toString);
}
