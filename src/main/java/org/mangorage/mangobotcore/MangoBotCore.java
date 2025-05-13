package org.mangorage.mangobotcore;

import org.mangorage.bootstrap.api.loader.MangoLoader;
import org.mangorage.mangobotcore.plugin.api.MangoBotPlugin;
import org.mangorage.mangobotcore.plugin.api.Plugin;
import org.mangorage.mixin.core.MixinServiceMangoBot;

import static org.mangorage.mangobotcore.MangoBotCore.ID;

@MangoBotPlugin(id = ID)
public final class MangoBotCore implements Plugin {

    public static final String ID = "mangobotcore";

    public static boolean isDevMode() {
        return org.mangorage.entrypoint.MangoBotCore.isDevMode();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void load() {
        var a = 1;
        new ExampleThing().load();

        MangoLoader loader = (MangoLoader) Thread.currentThread().getContextClassLoader();
        var bytes = MixinServiceMangoBot.getClassNode(
                loader.getClassBytes(
                        ExampleThing.class.getName()
                )
        );

        
        var aa = 1;
    }
}
