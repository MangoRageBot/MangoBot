package org.mangorage.mangobotcore;

import org.mangorage.mangobotcore.plugin.api.MangoBotPlugin;
import org.mangorage.mangobotcore.plugin.api.Plugin;

import static org.mangorage.mangobotcore.MangoBotCore.ID;

@MangoBotPlugin(id = ID)
public final class MangoBotCore implements Plugin {

    public static final String ID = "mangobotcore";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void load() {
        var a = 1;
    }
}
