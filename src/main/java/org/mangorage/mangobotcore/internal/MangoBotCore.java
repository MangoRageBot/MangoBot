package org.mangorage.mangobotcore.internal;

import org.mangorage.mangobotcore.api.plugin.v1.MangoBotPlugin;
import org.mangorage.mangobotcore.api.plugin.v1.Plugin;

import static org.mangorage.mangobotcore.internal.MangoBotCore.ID;

@MangoBotPlugin(id = ID)
public final class MangoBotCore implements Plugin {

    public static final String ID = "mangobotcore";

    public static boolean isDevMode() {
        return org.mangorage.mangobotcore.entrypoint.MangoBotCore.isDevMode();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void load() {
        var a = 1;
        new ExampleThing().load();
    }
}
